$ErrorActionPreference = 'Stop'

$serverDir = 'C:\Program Files\MySQL\MySQL Server 8.4'
$dataDir = 'C:\Users\ffy\AppData\Local\FoodOrdering\mysql-data'
$mysql = Join-Path $serverDir 'bin\mysql.exe'
$mysqld = Join-Path $serverDir 'bin\mysqld.exe'

if (-not (Test-Path $mysqld)) {
    throw "找不到 MySQL 服务器：$mysqld"
}

if (Test-NetConnection 127.0.0.1 -Port 3306 -InformationLevel Quiet) {
    Write-Output 'MySQL 已经在 127.0.0.1:3306 运行。'
    exit 0
}

New-Item -ItemType Directory -Path $dataDir -Force | Out-Null
$stdout = Join-Path $dataDir 'server.stdout.log'
$stderr = Join-Path $dataDir 'server.stderr.log'
Start-Process -FilePath $mysqld `
    -ArgumentList @("--datadir=$dataDir", '--port=3306', '--bind-address=127.0.0.1', '--console') `
    -WindowStyle Hidden `
    -RedirectStandardOutput $stdout `
    -RedirectStandardError $stderr | Out-Null

for ($i = 0; $i -lt 15; $i++) {
    if (Test-NetConnection 127.0.0.1 -Port 3306 -InformationLevel Quiet) {
        Write-Output 'MySQL 已启动：127.0.0.1:3306'
        exit 0
    }
    Start-Sleep -Seconds 2
}

Write-Output "MySQL 启动失败，请查看：$stderr"
exit 1
