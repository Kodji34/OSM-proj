# Script pour ajouter Java et Maven au PATH manuellement
# À exécuter en mode Administrateur si Java et Maven sont déjà installés

$javaHome = "C:\Program Files\Java\jdk-17"
$mavenHome = "C:\apache-maven"

Write-Host "=== Configuration du PATH ===" -ForegroundColor Green

# Vérifier les installations
if (Test-Path $javaHome) {
    Write-Host "✓ Java trouvé: $javaHome" -ForegroundColor Green
} else {
    Write-Host "✗ Java non trouvé. Cherchez l'installation Java:" -ForegroundColor Red
    Get-ChildItem "C:\Program Files" -Filter "*jdk*" -Directory
}

if (Test-Path $mavenHome) {
    Write-Host "✓ Maven trouvé: $mavenHome" -ForegroundColor Green
} else {
    Write-Host "✗ Maven non trouvé" -ForegroundColor Red
}

# Ajouter au PATH (session courante)
Write-Host "`nAjout au PATH (session courante)..." -ForegroundColor Cyan
$env:JAVA_HOME = $javaHome
$env:MAVEN_HOME = $mavenHome
$env:Path += ";$($javaHome)\bin;$($mavenHome)\bin"

Write-Host "Vérification:" -ForegroundColor Yellow
java -version
mvn -version

Write-Host "`n⚠ Note: Ces changements sont temporaires. Pour les rendre permanents:" -ForegroundColor Yellow
Write-Host "1. Ouvrir 'Paramètres' → 'Système' → 'Paramètres système avancés'"
Write-Host "2. Cliquer 'Variables d'environnement'"
Write-Host "3. Créer/modifier:"
Write-Host "   - JAVA_HOME = $javaHome"
Write-Host "   - MAVEN_HOME = $mavenHome"
Write-Host "   - Path += $($javaHome)\bin;$($mavenHome)\bin"
