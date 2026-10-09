"""Package an already tested web build, with the exact GitHub run identity."""
import io
import json
from pathlib import Path
import re
import sys
import tarfile
import time

REQUIRED = ('index.html', 'vk-billing.js', 'html/html.nocache.js', 'assets/assets.txt')


def package(dist, output, sha, run, attempt):
    dist, output = Path(dist), Path(output)
    if not re.fullmatch(r'[0-9a-f]{40}', sha) or not re.fullmatch(r'[1-9][0-9]{0,12}', str(run)) or not re.fullmatch(r'[1-9][0-9]{0,12}', str(attempt)):
        raise ValueError('Invalid deployment identity')
    for name in REQUIRED:
        if not (dist / name).is_file():
            raise ValueError('Incomplete web build: ' + name)
    if not list((dist / 'html').glob('*.cache.js')):
        raise ValueError('Compiled game is missing')
    if 'src="vk-billing.js"' not in (dist / 'index.html').read_text(encoding='utf-8'):
        raise ValueError('VK billing is missing from index.html')
    output.parent.mkdir(parents=True, exist_ok=True)
    metadata = json.dumps({'commit': sha, 'run': int(run), 'attempt': int(attempt)}, sort_keys=True).encode('utf-8')
    with tarfile.open(output, 'w:gz') as archive:
        for path in sorted(dist.rglob('*')):
            relative = path.relative_to(dist)
            if relative.parts[0] in ('WEB-INF', 'META-INF'):
                continue
            if path.is_symlink():
                raise ValueError('Symlinks are not allowed: ' + str(relative))
            if path.is_file() and relative.as_posix() != 'release.json':
                archive.add(path, arcname=relative.as_posix(), recursive=False)
        entry = tarfile.TarInfo('release.json')
        entry.size, entry.mode, entry.mtime = len(metadata), 0o644, int(time.time())
        archive.addfile(entry, io.BytesIO(metadata))
    print('Prepared web release:', output)


if __name__ == '__main__':
    if len(sys.argv) != 6:
        sys.exit('Usage: package-web.py <dist> <archive> <commit SHA> <run> <attempt>')
    package(*sys.argv[1:])
