param(
    [switch]$SkipBuild = $false
)

# Configuration des chemins
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
$env:MAVEN_HOME = "$env:USERPROFILE\maven"
$env:Path = "$env:Path;$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin"

Write-Host "========================================" -ForegroundColor Green
Write-Host "School Management - Application Web" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

# Vérifier Java
Write-Host "`nVérification Java..." -ForegroundColor Cyan
java -version
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERREUR: Java n'est pas trouvé" -ForegroundColor Red
    exit 1
}

# Vérifier Maven
Write-Host "`nVérification Maven..." -ForegroundColor Cyan
mvn -version
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERREUR: Maven n'est pas trouvé" -ForegroundColor Red
    exit 1
}

# Compiler
if (-not $SkipBuild) {
    Write-Host "`nCompilation du projet..." -ForegroundColor Cyan
    cd "C:\Users\hp\.vscode\appOSMJava"
    mvn clean install -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERREUR: La compilation a échoué" -ForegroundColor Red
        exit 1
    }
}

# Lancer
Write-Host "`n========================================" -ForegroundColor Green
Write-Host "Lancement de l'application..." -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "`nAccès: http://localhost:8080" -ForegroundColor Yellow
Write-Host "Identifiants: superadmin / admin" -ForegroundColor Yellow
Write-Host "`nAppuyez sur Ctrl+C pour arrêter l'application" -ForegroundColor Yellow
Write-Host "========================================`n" -ForegroundColor Green

cd "C:\Users\hp\.vscode\appOSMJava"
mvn spring-boot:run
