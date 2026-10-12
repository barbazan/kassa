"""Exercise packaging and rollback in a temporary directory, without SSH or HTTP."""
from contextlib import nullcontext
from datetime import datetime, timezone
import hashlib
import importlib.util
import io
import json
from pathlib import Path
import shutil
import tarfile
import tempfile
import unittest
import zipfile
import urllib.error
from types import SimpleNamespace
from contextlib import redirect_stdout
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[2]
SHA = 'a' * 40


def load(name, filename):
    spec = importlib.util.spec_from_file_location(name, ROOT / 'scripts' / filename)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


receiver = load('receiver', 'receive-web-deploy.py')
packager = load('packager', 'package-web.py')
gate = load('gate', 'check-web-platform.py')
sender = load('sender', 'deploy-web-artifact.py')


class PlatformSelectionTests(unittest.TestCase):
    def test_vk_declaration_enables_deployment(self):
        source = 'public static TargetPlatform TARGET_PLATFORM = TargetPlatform.HTML_VK;'
        self.assertEqual('HTML_VK', gate.target_platform(source))

    def test_vk_declarations_in_comments_do_not_enable_another_platform(self):
        source = '// public static TargetPlatform TARGET_PLATFORM = TargetPlatform.HTML_VK;\n' \
                 '/* public static TargetPlatform TARGET_PLATFORM = TargetPlatform.HTML_VK; */\n' \
                 'public static TargetPlatform TARGET_PLATFORM = TargetPlatform.HTML_YANDEX;'
        self.assertEqual('HTML_YANDEX', gate.target_platform(source))

    def test_unrecognized_configuration_fails_closed(self):
        with self.assertRaises(ValueError):
            gate.target_platform('public static TargetPlatform TARGET_PLATFORM;')

class WebDeploymentTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix='kassa-web-test-')
        self.root = Path(self.temp.name).resolve()
        (self.root / '.git').mkdir()
        self.target = self.root / 'static' / 'kassa'
        self.target.mkdir(parents=True)
        (self.target / 'index.html').write_text('previous version')
        (self.target / 'obsolete.js').write_text('old file')
        (self.target / '.gitignore').write_text('*')
        self.dist = self.root / 'dist'
        for name, value in {'index.html': '<script src="vk-billing.js"></script><script src="ok-billing.js"></script>',
                            'vk-billing.js': 'billing', 'ok-billing.js': 'ok-billing', 'html/html.nocache.js': 'loader',
                            'html/game.cache.js': 'game', 'assets/assets.txt': 'assets'}.items():
            path = self.dist / name
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(value)
        self.calls = 0
        self.sync_failure = False

    def tearDown(self):
        self.temp.cleanup()

    def sync(self, source, target):
        self.assertTrue(target.resolve().is_relative_to(self.root))
        self.calls += 1
        for child in target.iterdir():
            if child.name != '.gitignore':
                self.assertTrue(child.resolve().is_relative_to(target.resolve()))
                if child.is_dir():
                    shutil.rmtree(child)
                else:
                    child.unlink()
        shutil.copytree(source, target, dirs_exist_ok=True)
        if self.sync_failure and self.calls == 1:
            raise RuntimeError('simulated partial transfer failure')

    def archive(self, run=2, attempt=1):
        output = self.root / 'build.tar.gz'
        packager.package(self.dist, output, SHA, str(run), str(attempt))
        return output.read_bytes()

    def deploy(self, data=None, run=2, attempt=1, verify_error=None, digest=None):
        if data is None:
            data = self.archive(run, attempt)
        digest = digest or hashlib.sha256(data).hexdigest()
        command = 'deploy-web %s %s %s %s' % (SHA, digest, run, attempt)
        with patch.object(receiver, 'deployment_lock', return_value=nullcontext()), \
                patch.object(receiver, 'sync_files', side_effect=self.sync), \
                patch.object(receiver, 'verify_public_files', side_effect=verify_error):
            receiver.deploy(self.root, 'https://example.invalid/kassa/', command, io.BytesIO(data))

    def assert_previous_version(self):
        self.assertEqual('previous version', (self.target / 'index.html').read_text())
        self.assertTrue((self.target / 'obsolete.js').exists())
        self.assertFalse((self.root / '.git' / 'kassa-web-deployed.json').exists())
        self.assertFalse((self.target / 'deployment.json').exists())

    def test_success_publishes_verified_files_and_preserves_gitignore(self):
        self.deploy()
        self.assertEqual('billing', (self.target / 'vk-billing.js').read_text())
        self.assertEqual('ok-billing', (self.target / 'ok-billing.js').read_text())
        self.assertFalse((self.target / 'obsolete.js').exists())
        self.assertEqual('*', (self.target / '.gitignore').read_text())
        state = json.loads((self.root / '.git' / 'kassa-web-deployed.json').read_text())
        self.assertEqual({'commit': SHA, 'run': 2, 'attempt': 1}, state)
        deployment = json.loads((self.target / 'deployment.json').read_text())
        self.assertEqual(SHA, deployment['commit'])
        self.assertEqual(2, deployment['run'])
        self.assertIsNotNone(datetime.fromisoformat(deployment['deployedAt']).tzinfo)
        self.assertLess(abs((datetime.now(timezone.utc) - datetime.fromisoformat(deployment['deployedAt'])).total_seconds()), 10)
        game = json.loads((self.target / 'game.json').read_text(encoding='utf-8'))
        self.assertEqual('Касса', game['name'])
        self.assertTrue((self.target / game['icon']).is_file())
        self.assertEqual(1, len(list((self.root / '.kassa-web-backups').glob('*.tar.gz'))))

    def test_public_probe_failure_rolls_back_and_remains_failed(self):
        with self.assertRaisesRegex(RuntimeError, 'public probe'):
            self.deploy(verify_error=RuntimeError('public probe failed'))
        self.assert_previous_version()
        self.assertEqual(2, self.calls)

    def test_failed_deployment_restores_previous_success_timestamp(self):
        old = json.dumps({'commit': 'b' * 40, 'deployedAt': '2026-10-01T00:00:00Z'})
        (self.target / 'deployment.json').write_text(old)
        with self.assertRaisesRegex(RuntimeError, 'public probe'):
            self.deploy(verify_error=RuntimeError('public probe failed'))
        self.assertEqual(old, (self.target / 'deployment.json').read_text())
        self.assertEqual('previous version', (self.target / 'index.html').read_text())

    def test_failure_to_record_success_rolls_back_the_game_and_timestamp(self):
        old = json.dumps({'commit': 'b' * 40, 'deployedAt': '2026-10-01T00:00:00Z'})
        (self.target / 'deployment.json').write_text(old)
        with patch.object(receiver.os, 'replace', side_effect=OSError('record unavailable')):
            with self.assertRaisesRegex(OSError, 'record unavailable'):
                self.deploy()
        self.assertEqual(old, (self.target / 'deployment.json').read_text())
        self.assertEqual('previous version', (self.target / 'index.html').read_text())
        self.assertFalse((self.root / '.git' / 'kassa-web-deployed.json').exists())

    def test_partial_transfer_failure_rolls_back(self):
        self.sync_failure = True
        with self.assertRaisesRegex(RuntimeError, 'partial transfer'):
            self.deploy()
        self.assert_previous_version()
        self.assertEqual(2, self.calls)

    def test_wrong_archive_digest_never_changes_public_files(self):
        with self.assertRaisesRegex(ValueError, 'SHA-256'):
            self.deploy(digest='0' * 64)
        self.assert_previous_version()
        self.assertEqual(0, self.calls)

    def test_wrong_run_identity_never_changes_public_files(self):
        with self.assertRaisesRegex(ValueError, 'does not match'):
            self.deploy(data=self.archive(run=3))
        self.assert_previous_version()

    def test_path_traversal_and_links_are_rejected_before_publication(self):
        for name, kind in (('../escape', tarfile.REGTYPE), ('/absolute', tarfile.REGTYPE),
                           ('link', tarfile.SYMTYPE), ('hardlink', tarfile.LNKTYPE)):
            with self.subTest(name=name):
                output = io.BytesIO()
                with tarfile.open(fileobj=output, mode='w:gz') as archive:
                    entry = tarfile.TarInfo(name)
                    entry.type = kind
                    entry.linkname = '../outside'
                    archive.addfile(entry)
                with self.assertRaisesRegex(ValueError, 'Unsafe archive'):
                    self.deploy(data=output.getvalue())
                self.assert_previous_version()

    def test_older_workflow_run_cannot_replace_a_newer_release(self):
        self.deploy(run=3)
        self.deploy(run=2)
        self.assertEqual(1, self.calls)
        state = json.loads((self.root / '.git' / 'kassa-web-deployed.json').read_text())
        self.assertEqual(3, state['run'])

    def test_new_attempt_of_the_same_run_can_deploy(self):
        self.deploy(attempt=1)
        self.deploy(attempt=2)
        self.assertEqual(2, self.calls)

    def test_shell_commands_and_malformed_deployment_commands_are_rejected(self):
        for command in ('bash', 'scp -t /tmp', 'check; echo injected', 'deploy-web ' + SHA + '; touch /tmp/pwned'):
            with self.assertRaises(ValueError):
                receiver.parse_command(command)

    def test_incomplete_build_is_not_packaged(self):
        (self.dist / 'vk-billing.js').unlink()
        with self.assertRaisesRegex(ValueError, 'Incomplete web build'):
            self.archive()

    def test_servlet_metadata_is_not_published(self):
        (self.dist / 'WEB-INF').mkdir()
        (self.dist / 'WEB-INF' / 'web.xml').write_text('servlet metadata')
        self.deploy()
        self.assertFalse((self.target / 'WEB-INF').exists())


class ArtifactDeploymentTests(WebDeploymentTests):
    # Exercise the same publication and rollback scenarios through the HTTPS path.
    def deploy(self, data=None, run=2, attempt=1, verify_error=None, digest=None):
        if data is None:
            data = self.archive(run, attempt)
        zipped = io.BytesIO()
        with zipfile.ZipFile(zipped, 'w', zipfile.ZIP_DEFLATED) as archive:
            archive.writestr('kassa-web.tar.gz', data)
        digest = digest or hashlib.sha256(data).hexdigest()
        command = 'deploy-web-artifact %s %s %s %s' % (SHA, digest, run, attempt)
        request = io.BytesIO(json.dumps({'url': ArtifactTransportTests.URL}).encode())
        with patch.object(receiver, 'download_artifact', side_effect=lambda url, target: target.write_bytes(zipped.getvalue())), \
                patch.object(receiver, 'deployment_lock', return_value=nullcontext()), \
                patch.object(receiver, 'sync_files', side_effect=self.sync), \
                patch.object(receiver, 'verify_public_files', side_effect=verify_error):
            receiver.deploy(self.root, 'https://example.invalid/kassa/', command, request)


class ArtifactTransportTests(unittest.TestCase):
    URL = 'https://productionresultssa1.blob.core.windows.net/actions/build.zip?sig=synthetic'

    def test_only_https_storage_urls_without_credentials_or_redirects_are_allowed(self):
        self.assertEqual(self.URL, receiver.validate_artifact_url(self.URL))
        self.assertTrue(receiver.validate_artifact_url('https://results-receiver.actions.githubusercontent.com/build'))
        for url in ('http://127.0.0.1/', 'https://localhost/', 'https://169.254.169.254/',
                    'https://evil.example/', self.URL.replace('.net/', '.net.evil.example/'),
                    self.URL.replace('https://', 'https://user:pass@'), self.URL + '#fragment',
                    self.URL.replace('.net/', '.net:8443/'), self.URL + '\n', None):
            with self.subTest(url=url), self.assertRaises(ValueError):
                receiver.validate_artifact_url(url)
        self.assertIsNone(receiver.NoRedirect().redirect_request(None, None, 302, '', {}, 'http://localhost'))
        self.assertIsNone(sender.NoRedirect().redirect_request(None, None, 302, '', {}, 'https://evil.example'))

    def test_signed_url_is_not_exposed_on_http_failure(self):
        failure = urllib.error.HTTPError(self.URL, 403, 'expired signed URL', {}, None)
        with patch.object(receiver.urllib.request, 'build_opener') as opener:
            opener.return_value.open.side_effect = failure
            with self.assertRaises(receiver.DownloadError) as raised:
                receiver.download_artifact(self.URL, Path('unused'))
        self.assertNotIn('sig=', str(raised.exception))
        self.assertIn('403', str(raised.exception))

    def test_complete_download_incomplete_transfer_and_size_limit(self):
        with tempfile.TemporaryDirectory() as folder:
            target = Path(folder) / 'artifact.zip'
            for declared, limit, error in ((3, 10, None), (4, 10, receiver.DownloadError),
                                           (3, 2, ValueError), (None, 2, ValueError)):
                response = io.BytesIO(b'zip')
                response.status = 200
                response.headers = {} if declared is None else {'Content-Length': str(declared)}
                with patch.object(receiver.urllib.request, 'build_opener') as opener, \
                        patch.object(receiver, 'MAX_ARTIFACT', limit):
                    opener.return_value.open.return_value = response
                    if error:
                        with self.assertRaises(error): receiver.download_artifact(self.URL, target)
                    else:
                        receiver.download_artifact(self.URL, target)
                        self.assertEqual(b'zip', target.read_bytes())

    def test_zip_must_contain_only_the_checked_release_tar(self):
        with tempfile.TemporaryDirectory() as folder:
            target = Path(folder) / 'release.tar.gz'
            for files in ({'../kassa-web.tar.gz': b'x'}, {'kassa-web.tar.gz': b'x', 'extra': b'y'},
                          {'kassa-web.tar.gz': b'too large'}):
                zipped = io.BytesIO()
                with zipfile.ZipFile(zipped, 'w') as archive:
                    for name, data in files.items(): archive.writestr(name, data)
                with patch.object(receiver, 'download_artifact', side_effect=lambda url, file: file.write_bytes(zipped.getvalue())), \
                        patch.object(receiver, 'MAX_ARCHIVE', 2), self.assertRaises(ValueError):
                    receiver.receive_artifact(io.BytesIO(json.dumps({'url': self.URL}).encode()), target, '0'*64)

    def test_request_size_and_unknown_fields_are_rejected(self):
        for request in (b'x'*20001, b'[]', b'{"url":"test","token":"must-not-send"}'):
            with self.assertRaises(ValueError):
                receiver.receive_artifact(io.BytesIO(request), Path('unused'), '0'*64)

    def test_temporary_transport_errors_retry_with_new_urls_without_sending_token(self):
        with tempfile.TemporaryDirectory() as folder:
            archive = Path(folder) / 'build.tar.gz'; archive.write_bytes(b'archive')
            with patch.object(sender, 'artifact_url', side_effect=[self.URL+'1', self.URL+'2', self.URL+'3']) as url, \
                    patch.object(sender.subprocess, 'run', side_effect=[SimpleNamespace(returncode=255), SimpleNamespace(returncode=75), SimpleNamespace(returncode=0)]) as ssh, \
                    patch.object(sender.time, 'sleep'):
                sender.send(archive, 'key', 'known_hosts', SHA, '2', '1', 'owner/repo', '123', 'private-token')
                self.assertEqual(3, url.call_count)
                for index, call in enumerate(ssh.call_args_list, 1):
                    self.assertNotIn('sig=', ' '.join(call.args[0]))
                    self.assertNotIn('private-token', call.kwargs['input'].decode())
                    self.assertEqual({'url': self.URL+str(index)}, json.loads(call.kwargs['input']))

    def test_validation_failure_is_not_retried_and_retries_are_bounded(self):
        with tempfile.TemporaryDirectory() as folder:
            archive = Path(folder) / 'build.tar.gz'; archive.write_bytes(b'archive')
            for code, count in ((1, 1), (255, 3), (75, 3)):
                with patch.object(sender, 'artifact_url', return_value=self.URL), \
                        patch.object(sender.subprocess, 'run', return_value=SimpleNamespace(returncode=code)) as ssh, \
                        patch.object(sender.time, 'sleep'), self.assertRaises(RuntimeError):
                    sender.send(archive, 'key', 'known_hosts', SHA, '2', '1', 'owner/repo', '123', 'private-token')
                self.assertEqual(count, ssh.call_count)

    def test_github_redirect_is_masked_and_not_followed_with_the_token(self):
        response = urllib.error.HTTPError('https://api.github.com/', 302, '', {'Location': self.URL}, None)
        with patch.object(sender.urllib.request, 'build_opener') as opener, redirect_stdout(io.StringIO()) as output:
            opener.return_value.open.side_effect = response
            self.assertEqual(self.URL, sender.artifact_url('owner/repo', '123', 'private-token'))
            self.assertEqual(1, opener.return_value.open.call_count)
        self.assertIn('::add-mask::', output.getvalue())
        self.assertNotIn('private-token', output.getvalue())


if __name__ == '__main__':
    unittest.main()
