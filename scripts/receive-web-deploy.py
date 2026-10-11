#!/usr/bin/env python3
"""Forced SSH command for the kassa web deployment key; installed outside Git."""
from contextlib import contextmanager
from datetime import datetime, timezone
import hashlib
import json
import os
from pathlib import Path, PurePosixPath
import re
import shutil
import subprocess
import sys
import tarfile
import tempfile
import time
import urllib.request
import urllib.error
from urllib.parse import urlsplit
import zipfile

PROJECT_ROOT = Path(os.environ.get('KASSA_DEPLOY_ROOT', '/home/codex/mir-server'))
SITE_URL = os.environ.get('KASSA_DEPLOY_URL', 'https://mir-game.ru/kassa/')
REQUIRED = ('index.html', 'vk-billing.js', 'html/html.nocache.js', 'assets/assets.txt', 'release.json')
MAX_ARCHIVE = 200 * 1024 * 1024
MAX_EXTRACTED = 512 * 1024 * 1024
MAX_ARTIFACT = MAX_ARCHIVE + 1024 * 1024


class DownloadError(Exception):
    """A temporary HTTPS failure; safe to retry with a fresh signed URL."""


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, request, fp, code, message, headers, new_url):
        return None


def validate_artifact_url(url):
    if not isinstance(url, str) or len(url) > 16384 or any(ord(c) <= 32 or ord(c) > 126 for c in url):
        raise ValueError('Invalid artifact URL')
    parts = urlsplit(url)
    host = parts.hostname or ''
    allowed = (re.fullmatch(r'[a-z0-9]+\.blob\.core\.windows\.net', host)
               or re.fullmatch(r'[a-z0-9.-]+\.actions\.githubusercontent\.com', host))
    if (parts.scheme != 'https' or not allowed or parts.port not in (None, 443)
            or parts.username or parts.password or parts.fragment):
        raise ValueError('Only HTTPS GitHub artifact storage URLs are allowed')
    return url


def download_artifact(url, destination):
    url = validate_artifact_url(url)
    request = urllib.request.Request(url, headers={'User-Agent': 'kassa-web-deploy'})
    started = progress = time.monotonic()
    total = 0
    print('Downloading build from GitHub over HTTPS...', flush=True)
    try:
        # Never follow a redirect to another host or print the signed URL.
        with urllib.request.build_opener(NoRedirect()).open(request, timeout=45) as response:
            if response.status != 200:
                raise DownloadError('Artifact download returned an unexpected HTTP status')
            length = response.headers.get('Content-Length')
            if length is not None and int(length) > MAX_ARTIFACT:
                raise ValueError('Artifact exceeds the download limit')
            with destination.open('wb') as output:
                while True:
                    chunk = response.read1(64 * 1024)
                    if not chunk:
                        break
                    total += len(chunk)
                    if total > MAX_ARTIFACT:
                        raise ValueError('Artifact exceeds the download limit')
                    output.write(chunk)
                    now = time.monotonic()
                    if now - started > 900:
                        raise DownloadError('Artifact download exceeded 15 minutes')
                    if now - progress >= 5:
                        print('Downloaded %.1f MiB in %d seconds' % (total / 1024**2, now - started), flush=True)
                        progress = now
            if length is not None and total != int(length):
                raise DownloadError('Artifact download was incomplete')
    except urllib.error.HTTPError as error:
        raise DownloadError('Artifact download HTTP status %d' % error.code) from None
    except (urllib.error.URLError, TimeoutError, ConnectionError) as error:
        raise DownloadError('Artifact download connection failed (%s)' % type(error).__name__) from None
    print('Build downloaded: %.1f MiB in %.1f seconds' % (total / 1024**2, time.monotonic() - started), flush=True)


def receive_artifact(stream, destination, expected):
    request = stream.read(20001)
    if len(request) > 20000:
        raise ValueError('Artifact request is too large')
    metadata = json.loads(request)
    if not isinstance(metadata, dict) or set(metadata) != {'url'}:
        raise ValueError('Artifact request must contain only its signed URL')
    zip_path = destination.with_suffix('.zip')
    download_artifact(metadata['url'], zip_path)
    with zipfile.ZipFile(zip_path) as artifact:
        entries = artifact.infolist()
        if (len(entries) != 1 or entries[0].filename != 'kassa-web.tar.gz'
                or entries[0].is_dir() or entries[0].file_size > MAX_ARCHIVE
                or entries[0].flag_bits & 1):
            raise ValueError('Artifact must contain only kassa-web.tar.gz within the size limit')
        with artifact.open(entries[0]) as archive:
            receive_archive(archive, destination, expected)


def parse_command(command):
    match = re.fullmatch(r'deploy-web(?:-artifact)? ([0-9a-f]{40}) ([0-9a-f]{64}) ([1-9][0-9]{0,12}) ([1-9][0-9]{0,12})', command)
    if not match:
        raise ValueError('Only check and deploy-web <SHA> <archive SHA-256> <run> <attempt> are allowed')
    sha, digest, run, attempt = match.groups()
    return sha, digest, int(run), int(attempt)


def validate_target(root):
    root = Path(root).resolve(strict=True)
    target = root / 'static' / 'kassa'
    if not (root / '.git').is_dir() or not target.is_dir():
        raise ValueError('Existing mir-server checkout and kassa directory are required')
    if (root / 'static').is_symlink() or target.is_symlink() or target.resolve() != target:
        raise ValueError('Deployment target must not be redirected through a symlink')
    return root, target


@contextmanager
def deployment_lock(root):
    import fcntl  # The receiver runs on the Linux server.
    with (root / '.git' / 'mir-deploy.lock').open('a') as lock:
        deadline = time.monotonic() + 900
        while True:
            try:
                fcntl.flock(lock, fcntl.LOCK_EX | fcntl.LOCK_NB)
                break
            except BlockingIOError:
                if time.monotonic() >= deadline:
                    raise TimeoutError('Another server or web deployment holds the lock')
                time.sleep(0.5)
        yield


def receive_archive(stream, destination, expected):
    digest, total = hashlib.sha256(), 0
    with destination.open('wb') as output:
        while True:
            chunk = stream.read(1024 * 1024)
            if not chunk:
                break
            total += len(chunk)
            if total > MAX_ARCHIVE:
                raise ValueError('Archive exceeds 200 MiB')
            digest.update(chunk)
            output.write(chunk)
    if digest.hexdigest() != expected:
        raise ValueError('Archive SHA-256 does not match')


def extract_files(archive_path, destination):
    destination.mkdir(parents=True, exist_ok=True)
    seen, total = set(), 0
    with tarfile.open(archive_path, 'r:gz') as archive:
        for member in archive:
            path = PurePosixPath(member.name)
            if path.is_absolute() or '..' in path.parts or '\\' in member.name or not (member.isfile() or member.isdir()):
                raise ValueError('Unsafe archive entry: ' + member.name)
            if not path.parts:
                if member.isdir():
                    continue
                raise ValueError('Invalid archive entry')
            name = path.as_posix()
            if name in seen or len(seen) >= 50000:
                raise ValueError('Duplicate entry or too many archive entries')
            seen.add(name)
            total += member.size
            if total > MAX_EXTRACTED:
                raise ValueError('Extracted archive exceeds 512 MiB')
            output = destination.joinpath(*path.parts)
            if member.isdir():
                output.mkdir(parents=True, exist_ok=True)
            else:
                output.parent.mkdir(parents=True, exist_ok=True)
                with archive.extractfile(member) as source, output.open('wb') as target:
                    shutil.copyfileobj(source, target)
                output.chmod(0o644)


def validate_release(directory, sha, run, attempt):
    for name in REQUIRED:
        if not (directory / name).is_file():
            raise ValueError('Incomplete archive: ' + name)
    if not list((directory / 'html').glob('*.cache.js')):
        raise ValueError('Compiled game is missing')
    if 'src="vk-billing.js"' not in (directory / 'index.html').read_text(encoding='utf-8'):
        raise ValueError('VK billing is missing from index.html')
    expected = {'commit': sha, 'run': run, 'attempt': attempt}
    if json.loads((directory / 'release.json').read_text()) != expected:
        raise ValueError('Archive does not match the requested commit and workflow run')


def sync_files(source, target):
    subprocess.run(['rsync', '-a', '--delay-updates', '--delete-delay', '--chmod=D755,F644',
                    '--exclude=.gitignore', str(source) + '/', str(target) + '/'], check=True)


def verify_public_files(directory, site_url):
    for name in ('index.html', 'vk-billing.js', 'html/html.nocache.js', 'release.json'):
        expected = (directory / name).read_bytes()
        request = urllib.request.Request(site_url + name + '?deploy=' + str(time.time_ns()),
                                         headers={'Cache-Control': 'no-cache', 'User-Agent': 'kassa-web-deploy'})
        with urllib.request.urlopen(request, timeout=30) as response:
            actual = response.read(len(expected) + 1)
        if actual != expected:
            raise ValueError('Published file differs from the uploaded build: ' + name)


def deploy(root, site_url, command, stream):
    sha, digest, run, attempt = parse_command(command)
    root, target = validate_target(root)
    state_path = root / '.git' / 'kassa-web-deployed.json'
    with tempfile.TemporaryDirectory(prefix='kassa-web-', dir=root / '.git') as temporary:
        work = Path(temporary)
        archive = work / 'release.tar.gz'
        if command.startswith('deploy-web-artifact '):
            receive_artifact(stream, archive, digest)
        else:
            receive_archive(stream, archive, digest)
        new = work / 'new'
        extract_files(archive, new)
        validate_release(new, sha, run, attempt)
        with deployment_lock(root):
            if state_path.exists():
                previous = json.loads(state_path.read_text())
                if (run, attempt) <= (previous['run'], previous['attempt']):
                    print('Skipping an already deployed or superseded workflow run.')
                    return
            backup_dir = root / '.kassa-web-backups'
            backup_dir.mkdir(exist_ok=True)
            backup = backup_dir / ('%s-%s-%s.tar.gz' % (run, attempt, sha))
            if backup.exists():
                raise ValueError('A backup for this run already exists; rerun the workflow to create a new attempt')
            for path in target.rglob('*'):
                if path.is_symlink():
                    raise ValueError('Existing web files must not contain symlinks')
            with tarfile.open(backup, 'w:gz') as saved:
                saved.add(target, arcname='.')
            print('Previous version saved:', backup, flush=True)
            try:
                sync_files(new, target)
                verify_public_files(new, site_url)
                # Written only after the public probes pass; rollback restores the old record.
                deployment = work / 'deployment.json'
                deployment.write_text(json.dumps({'commit': sha, 'run': run, 'attempt': attempt,
                    'deployedAt': datetime.now(timezone.utc).isoformat()}) + '\n')
                deployment.chmod(0o644)
                os.replace(deployment, target / 'deployment.json')
                state = work / 'deployed.json'
                state.write_text(json.dumps({'commit': sha, 'run': run, 'attempt': attempt}) + '\n')
                os.replace(state, state_path)
            except Exception:
                print('Deployment failed; restoring the previous version.', file=sys.stderr, flush=True)
                previous = work / 'previous'
                extract_files(backup, previous)
                sync_files(previous, target)
                print('Previous version restored. Deployment remains failed.', file=sys.stderr, flush=True)
                raise
            print('Kassa web version deployed:', site_url, sha, flush=True)


def main():
    command = os.environ.get('SSH_ORIGINAL_COMMAND', '')
    if command == 'check':
        root, target = validate_target(PROJECT_ROOT)
        if not shutil.which('rsync'):
            raise ValueError('rsync is required on the server')
        print('Kassa web deployment connection is ready:', target)
        return
    deploy(PROJECT_ROOT, SITE_URL, command, sys.stdin.buffer)


if __name__ == '__main__':
    try:
        main()
    except DownloadError as error:
        print('Web deployment download failed:', error, file=sys.stderr)
        sys.exit(75)
    except Exception as error:
        print('Web deployment failed:', error, file=sys.stderr)
        sys.exit(1)
