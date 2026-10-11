#!/usr/bin/env python3
"""Send a short-lived artifact URL over SSH; the server downloads the build via HTTPS."""
import hashlib
import json
import os
from pathlib import Path
import re
import subprocess
import sys
import time
import urllib.error
import urllib.request


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, request, fp, code, message, headers, new_url):
        return None


def artifact_url(repository, artifact_id, token):
    if not re.fullmatch(r'[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+', repository) or not re.fullmatch(r'[1-9][0-9]*', artifact_id):
        raise ValueError('Invalid GitHub repository or artifact ID')
    request = urllib.request.Request(
        'https://api.github.com/repos/%s/actions/artifacts/%s/zip' % (repository, artifact_id),
        headers={'Authorization': 'Bearer ' + token, 'Accept': 'application/vnd.github+json',
                 'X-GitHub-Api-Version': '2022-11-28', 'User-Agent': 'kassa-web-deploy'})
    # Keep the GitHub token on the runner. Only the signed storage URL reaches the server.
    try:
        with urllib.request.build_opener(NoRedirect()).open(request, timeout=30):
            raise RuntimeError('GitHub did not return an artifact download URL')
    except urllib.error.HTTPError as error:
        if error.code != 302:
            raise RuntimeError('GitHub artifact API returned HTTP %d' % error.code) from None
        url = error.headers.get('Location', '')
    if not url.startswith('https://') or len(url) > 16384 or any(ord(c) <= 32 or ord(c) > 126 for c in url):
        raise RuntimeError('GitHub returned an invalid artifact download URL')
    # URLs grant temporary access; mask them before any child process runs.
    print('::add-mask::' + url.replace('%', '%25'), flush=True)
    return url


def send(archive, key, known_hosts, sha, run, attempt, repository, artifact_id, token):
    if (not re.fullmatch(r'[0-9a-f]{40}', sha)
            or not all(re.fullmatch(r'[1-9][0-9]{0,12}', value) for value in (run, attempt))):
        raise ValueError('Invalid deployment identity')
    digest = hashlib.sha256(Path(archive).read_bytes()).hexdigest()
    command = ['ssh', '-i', str(key), '-o', 'UserKnownHostsFile=' + str(known_hosts),
               '-o', 'StrictHostKeyChecking=yes', '-o', 'BatchMode=yes', '-o', 'ConnectTimeout=15',
               '-o', 'ServerAliveInterval=30', '-o', 'ServerAliveCountMax=3',
               'codex@194.87.101.68', 'deploy-web-artifact %s %s %s %s' % (sha, digest, run, attempt)]
    for number in range(1, 4):
        # The URL expires quickly, so obtain a new one for each transport retry.
        url = artifact_url(repository, artifact_id, token)
        print('Starting HTTPS deployment, connection attempt %d/3' % number, flush=True)
        result = subprocess.run(command, input=json.dumps({'url': url}).encode('utf-8'), check=False)
        if result.returncode == 0:
            return
        if result.returncode not in (75, 255) or number == 3:
            raise RuntimeError('Deployment failed with exit code %d' % result.returncode)
        print('Connection failed; retrying with a fresh download URL in 10 seconds.', flush=True)
        time.sleep(10)


def main():
    credentials = Path(os.environ['RUNNER_TEMP']) / 'kassa-web-ssh'
    send('build/kassa-web.tar.gz', credentials / 'id_ed25519', credentials / 'known_hosts',
         os.environ['DEPLOY_SHA'], os.environ['DEPLOY_RUN'], os.environ['DEPLOY_ATTEMPT'],
         os.environ['GITHUB_REPOSITORY'], os.environ['DEPLOY_ARTIFACT_ID'], os.environ['GH_TOKEN'])


if __name__ == '__main__':
    try:
        main()
    except Exception as error:
        print('Web deployment failed: %s' % error, file=sys.stderr)
        sys.exit(1)
