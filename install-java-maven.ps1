# Script d'installation automatique de Java 17 et Maven
# À exécuter en mode Administrateur

Write-Host "=== Installation Java 17 et Maven ===" -ForegroundColor Green

# 1. Télécharger et installer Java 17
Write-Host "`n[1/3] Installation Java 17..." -ForegroundColor Cyan
$javaUrl = "https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.exe"
$javaPath = "$env:TEMP\jdk-17.exe"

try {
    Write-Host "Téléchargement Java 17..."
    Invoke-WebRequest -Uri $javaUrl -OutFile $javaPath -UseBasicParsing
    Write-Host "Installation en cours (cela peut prendre 2-3 minutes)..."
    & $javaPath /s INSTALLDIR="C:\Program Files\Java\jdk-17" ADDLOCAL="FeatureList" REBOOT=0
    Write-Host "Java 17 installé avec succès!" -ForegroundColor Green
} catch {
    Write-Host "Erreur lors du téléchargement de Java: $_" -ForegroundColor Red
    Write-Host "Installez Java 17 manuellement: https://www.oracle.com/java/technologies/downloads/#java17"
}

# 2. Ajouter Java au PATH
Write-Host "`n[2/3] Configuration du PATH..." -ForegroundColor Cyan
$javaHome = "C:\Program Files\Java\jdk-17"
if (Test-Path $javaHome) {
    $currentPath = [Environment]::GetEnvironmentVariable("Path", "Machine")
    $javaPath = "$javaHome\bin"
    
    if ($currentPath -notlike "*$javaPath*") {
        [Environment]::SetEnvironmentVariable("Path", "$currentPath;$javaPath", "Machine")
        [Environment]::SetEnvironmentVariable("JAVA_HOME", $javaHome, "Machine")
        Write-Host "Java ajouté au PATH" -ForegroundColor Green
    }
}

# 3. Télécharger et installer Maven
Write-Host "`n[3/3] Installation Maven..." -ForegroundColor Cyan
$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
$mavenZip = "$env:TEMP\maven.zip"
$mavenHome = "C:\apache-maven"

try {
    Write-Host "Téléchargement Maven 3.9.6..."
    Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenZip -UseBasicParsing
    
    Write-Host "Extraction Maven..."
    if (Test-Path $mavenHome) {
        Remove-Item $mavenHome -Recurse -Force
    }
    Expand-Archive -Path $mavenZip -DestinationPath "C:\"
    Rename-Item "C:\apache-maven-3.9.6" $mavenHome
    
    # Ajouter Maven au PATH
    $currentPath = [Environment]::GetEnvironmentVariable("Path", "Machine")
    $mavenPath = "$mavenHome\bin"
    
    if ($currentPath -notlike "*$mavenPath*") {
        [Environment]::SetEnvironmentVariable("Path", "$currentPath;$mavenPath", "Machine")
        [Environment]::SetEnvironmentVariable("MAVEN_HOME", $mavenHome, "Machine")
        Write-Host "Maven installé et ajouté au PATH" -ForegroundColor Green
    }
    
    Remove-Item $mavenZip
} catch {
    Write-Host "Erreur lors du téléchargement de Maven: $_" -ForegroundColor Red
    Write-Host "Installez Maven manuellement: https://maven.apache.org/download.cgi"
}

Write-Host "`n=== Installation terminée ===" -ForegroundColor Green
Write-Host "`nVérification:" -ForegroundColor Yellow
Write-Host "Redémarrez PowerShell, puis testez:"
Write-Host "  java -version"
Write-Host "  mvn -version"
Write-Host "`nEnsuite, lancez: mvn spring-boot:run"
