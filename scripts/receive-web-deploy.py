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

PROJECT_ROOT = Path(os.environ.get('KASSA_DEPLOY_ROOT', '/home/codex/mir-server'))
SITE_URL = os.environ.get('KASSA_DEPLOY_URL', 'https://mir-game.ru/kassa/')
REQUIRED = ('index.html', 'vk-billing.js', 'html/html.nocache.js', 'assets/assets.txt', 'release.json')
MAX_ARCHIVE = 200 * 1024 * 1024
MAX_EXTRACTED = 512 * 1024 * 1024


def parse_command(command):
    match = re.fullmatch(r'deploy-web ([0-9a-f]{40}) ([0-9a-f]{64}) ([1-9][0-9]{0,12}) ([1-9][0-9]{0,12})', command)
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
    except Exception as error:
        print('Web deployment failed:', error, file=sys.stderr)
        sys.exit(1)
