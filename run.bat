@echo off
REM Script pour lancer l'application avec les variables d'environnement configurées

setlocal enabledelayedexpansion

REM Configurer les chemins
set "JAVA_HOME=C:\Program Files\Java\jdk-25"
set "MAVEN_HOME=%USERPROFILE%\maven"
set "PATH=%PATH%;%JAVA_HOME%\bin;%MAVEN_HOME%\bin"

REM Vérifier Java
echo Vérification Java...
java -version
if errorlevel 1 (
    echo ERREUR: Java n'est pas trouvé
    pause
    exit /b 1
)

REM Vérifier Maven
echo Vérification Maven...
mvn -version
if errorlevel 1 (
    echo ERREUR: Maven n'est pas trouvé
    pause
    exit /b 1
)

REM Compiler et lancer
cd /d "C:\Users\hp\.vscode\appOSMJava"
echo Compilation du projet...
call mvn clean install -DskipTests

if errorlevel 1 (
    echo ERREUR: La compilation a échoué
    pause
    exit /b 1
)

echo.
echo ========================================
echo Lancement de l'application...
echo Accès: http://localhost:8080
echo Identifiants: superadmin / admin
echo ========================================
echo.

call mvn spring-boot:run

pause
