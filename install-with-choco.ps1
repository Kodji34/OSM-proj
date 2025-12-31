# Installation simple via Chocolatey (si disponible)
# À exécuter en mode Administrateur

Write-Host "=== Installation via Chocolatey ===" -ForegroundColor Green

# Vérifier si Chocolatey est installé
if (-not (Get-Command choco -ErrorAction SilentlyContinue)) {
    Write-Host "Chocolatey n'est pas installé. Installation..." -ForegroundColor Yellow
    Set-ExecutionPolicy Bypass -Scope Process -Force
    [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
    iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
}

# Installer Java 17 et Maven
Write-Host "Installation de Java 17..." -ForegroundColor Cyan
choco install openjdk17 -y

Write-Host "Installation de Maven..." -ForegroundColor Cyan
choco install maven -y

Write-Host "`nInstallation terminée!" -ForegroundColor Green
Write-Host "Redémarrez PowerShell et testez:"
Write-Host "  java -version"
Write-Host "  mvn -version"
