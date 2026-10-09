[CmdletBinding()]
param(
    [switch]$Install,
    [string]$IdentityFile = "$env:USERPROFILE\.ssh\id_rsa"
)
$ErrorActionPreference = 'Stop'
$repoRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
$credentials = Join-Path $repoRoot 'build\github-web-deploy\credentials'
$keyPath = Join-Path $credentials 'id_ed25519'
$knownHostsPath = Join-Path $credentials 'known_hosts'
$personalKnownHosts = Join-Path $env:USERPROFILE '.ssh\known_hosts'
$keygen = (Get-Command ssh-keygen.exe -ErrorAction Stop).Source
$ssh = (Get-Command ssh.exe -ErrorAction Stop).Source
if (-not (Test-Path -LiteralPath $personalKnownHosts -PathType Leaf)) {
    throw 'A previously verified server entry in ~/.ssh/known_hosts is required.'
}
$hostRecords = @(& $keygen -F '194.87.101.68' -f $personalKnownHosts | Where-Object { $_ -and -not $_.StartsWith('#') })
if ($LASTEXITCODE -ne 0 -or $hostRecords.Count -eq 0) {
    throw 'No previously verified SSH host key for 194.87.101.68 was found.'
}
[System.IO.Directory]::CreateDirectory($credentials) | Out-Null
$utf8 = [System.Text.UTF8Encoding]::new($false)
[System.IO.File]::WriteAllText($knownHostsPath, ($hostRecords -join "`n") + "`n", $utf8)
if (-not (Test-Path -LiteralPath $keyPath)) {
    # Start-Process preserves the empty passphrase argument in Windows PowerShell 5.1.
    $keygenArgs = '-q -t ed25519 -N "" -C github-actions-kassa-web -f "' + $keyPath + '"'
    $process = Start-Process -FilePath $keygen -ArgumentList $keygenArgs -WindowStyle Hidden -Wait -PassThru
    if ($process.ExitCode -ne 0) { throw "SSH key generation failed: $($process.ExitCode)" }
}
$publicKey = [System.IO.File]::ReadAllText($keyPath + '.pub').Trim()
if ($publicKey -notmatch '^ssh-ed25519 [A-Za-z0-9+/=]+ github-actions-kassa-web$') {
    throw 'Unexpected deployment public key format.'
}
$receiverPath = Join-Path $PSScriptRoot 'receive-web-deploy.py'
$receiverBase64 = [Convert]::ToBase64String([System.IO.File]::ReadAllBytes($receiverPath))
$publicKeyBase64 = [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes($publicKey))
$bootstrap = @"
set -eu
python3 - <<'PY'
import base64
import os
from pathlib import Path
import shutil
root = Path('/home/codex/mir-server')
if not (root / '.git').is_dir() or not (root / 'static/kassa').is_dir():
    raise SystemExit('Existing mir-server checkout and kassa directory are required')
if not shutil.which('rsync'):
    raise SystemExit('rsync is required')
base = Path.home() / '.local/lib/kassa-web-deploy'
base.mkdir(parents=True, exist_ok=True)
base.chmod(0o700)
script = base / 'receive.py'
temporary = base / 'receive.py.new'
temporary.write_bytes(base64.b64decode('__RECEIVER__'))
temporary.chmod(0o600)
os.replace(temporary, script)
public_key = base64.b64decode('__PUBLIC_KEY__').decode('utf-8')
key_data = public_key.split()[1]
ssh_dir = Path.home() / '.ssh'
ssh_dir.mkdir(exist_ok=True)
ssh_dir.chmod(0o700)
authorized = ssh_dir / 'authorized_keys'
existing = authorized.read_text() if authorized.exists() else ''
line = 'restrict,command="/usr/bin/python3 /home/codex/.local/lib/kassa-web-deploy/receive.py" ' + public_key
matches = [entry for entry in existing.splitlines() if key_data in entry.split()]
if matches and matches != [line]:
    raise SystemExit('This key already exists with different SSH restrictions; inspect authorized_keys')
if not matches:
    with authorized.open('a') as output:
        if existing and not existing.endswith('\n'):
            output.write('\n')
        output.write(line + '\n')
authorized.chmod(0o600)
print('Restricted kassa web deployment key installed.')
PY
"@
$bootstrap = $bootstrap.Replace('__RECEIVER__', $receiverBase64).Replace('__PUBLIC_KEY__', $publicKeyBase64)
$bootstrapPath = Join-Path $credentials 'install.sh'
[System.IO.File]::WriteAllText($bootstrapPath, $bootstrap.Replace("`r`n", "`n") + "`n", $utf8)
Write-Host "Prepared files: $credentials"
if ($Install) {
    if (-not (Test-Path -LiteralPath $IdentityFile -PathType Leaf)) { throw "Personal SSH key not found: $IdentityFile" }
    $sshOptions = @('-o', "UserKnownHostsFile=$knownHostsPath", '-o', 'StrictHostKeyChecking=yes', '-o', 'BatchMode=yes', '-o', 'ConnectTimeout=15')
    $bootstrap | & $ssh -i $IdentityFile @sshOptions 'codex@194.87.101.68' "tr -d '\r' | bash -s"
    if ($LASTEXITCODE -ne 0) { throw "Server setup failed: $LASTEXITCODE" }
    & $ssh -i $keyPath @sshOptions 'codex@194.87.101.68' 'check'
    if ($LASTEXITCODE -ne 0) { throw "Deployment connection check failed: $LASTEXITCODE" }
}
Write-Host 'Add these two repository Actions secrets to barbazan/kassa:'
Write-Host "DEPLOY_SSH_KEY: contents of $keyPath"
Write-Host "DEPLOY_KNOWN_HOSTS: contents of $knownHostsPath"
Write-Host 'No game files have been published by this setup script.'
