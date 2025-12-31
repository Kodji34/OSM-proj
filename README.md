# OSM (Open Suku Manager) - Application Web

Application Spring Boot (Thymeleaf) pour la gestion SaaS d'etablissements scolaires avec authentification et autorisation par roles (SUPER_ADMIN / ADMIN / USER).

## Prerequis

- **Java 17+** : [Telecharger ici](https://www.oracle.com/java/technologies/downloads/#java17)
- **Maven 3.8+** : [Telecharger ici](https://maven.apache.org/download.cgi)
- **IDE** : VS Code + Extension Java ou IntelliJ IDEA

## Installation

### 1. Installer Java 17

**Windows** :
- Telecharger l'installeur depuis le lien ci-dessus
- Executer l'installeur et suivre les etapes
- Ajouter Java au PATH :
  - Ouvrir "Variables d'environnement systeme"
  - Ajouter `C:\Program Files\Java\jdk-17.x.x\bin` au PATH
- Verifier : `java -version`

### 2. Installer Maven

**Windows** :
- Telecharger Apache Maven (binary zip)
- Extraire vers `C:\Apache\maven` (ou dossier de votre choix)
- Ajouter le PATH :
  - Ajouter `C:\Apache\maven\bin` au PATH systeme
- Verifier : `mvn -version`

## Lancement

```bash
cd C:\Users\hp\.vscode\appOSMJava
mvn clean install
mvn spring-boot:run
```

L'application demarre sur : **http://localhost:8081**

## Authentification par defaut

- **Utilisateur** : `superadmin`
- **Mot de passe** : `admin`

## Endpoints API

### Authentification (HTTP Basic)

```bash
curl -u superadmin:admin -X GET http://localhost:8081/api/establishments
```

### Gestion des etablissements (apres authentification)

- `GET /api/establishments` - Lister tous les etablissements
- `GET /api/establishments/{id}` - Details d'un etablissement
- `POST /api/establishments` - Creer un etablissement
- `PUT /api/establishments/{id}` - Modifier un etablissement
- `DELETE /api/establishments/{id}` - Supprimer un etablissement

### Administration (ADMIN / SUPER_ADMIN)

#### Activer l'adhesion (paiement recu)

```bash
curl -u superadmin:admin -X POST \
  http://localhost:8081/api/admin/establishments/1/activate
```

#### Deployer et configurer un etablissement

```bash
curl -u superadmin:admin \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "ecole-centrale.example.com",
    "theme": "blue",
    "timezone": "Africa/Casablanca",
    "currency": "MAD"
  }' \
  -X POST http://localhost:8081/api/admin/establishments/1/deploy
```

Reponse (200 OK) :
```json
{
  "id": 1,
  "name": "Lycee Central",
  "address": "1 rue Principale",
  "type": { "id": 2, "name": "Secondaire" },
  "active": true,
  "deployed": true,
  "config": "{\"domain\": \"ecole-centrale.example.com\", ...}"
}
```

## Base de donnees

- **Developpement** : H2 (en memoire)
- **Console H2** : desactivee par defaut (activer `spring.h2.console.enabled=true` si besoin)
  - URL JDBC : `jdbc:h2:mem:schoolsdb`
  - Utilisateur : `sa`
  - Mot de passe : (vide)

## Architecture

```
src/main/java/com/example/school/
- SchoolManagementApplication.java      (point d'entree)
- entity/
  - Establishment.java
  - SchoolType.java
  - User.java
  - Role.java
  - Position.java
  - Employee.java
  - AcademicYear.java
- repository/
  - EstablishmentRepository.java
  - UserRepository.java
  - RoleRepository.java
  - PositionRepository.java
  - EmployeeRepository.java
  - SchoolTypeRepository.java
  - AcademicYearRepository.java
- service/
  - EstablishmentService.java
- controller/
  - HomeController.java
  - EstablishmentController.java
  - AdminEstablishmentController.java
- config/
  - SecurityConfig.java
- DataInitializer.java
```

## Logs et depannage

- Erreur : "mvn n'est pas reconnu"
  - Verifier que Maven est installe et dans le PATH
  - Redemarrer le terminal PowerShell/CMD apres installation
- Erreur : "Port 8080 deja utilise"
  - Changer le port dans `src/main/resources/application.properties` :
  ```properties
  server.port=8081
  ```
- Erreur : "Connection refused"
  - Verifier que l'application est bien lancee : `mvn spring-boot:run`
  - Verifier le port dans les logs (par defaut 8081)

## Developpement

- Profiles Spring : `dev` (defaut), `prod`
- Hot reload : Spring Boot DevTools
- Tests : `mvn test`

## Prochaines etapes

- [x] Scaffold et authentification
- [x] Endpoints API admin
- [x] Interface Thymeleaf
- [ ] Tests unitaires
- [ ] Docker

## Envoi d'emails (SMTP)

L'application peut envoyer de vrais emails lors de la creation des comptes (administrateur etablissement + personnel).

Configurer les variables suivantes (dans `src/main/resources/application.properties` ou via variables d'environnement) :

- `SMTP_HOST`
- `SMTP_PORT` (ex: 587)
- `SMTP_USERNAME`
- `SMTP_PASSWORD`
- `SMTP_STARTTLS` (true/false)
- `MAIL_FROM` (optionnel)
- `DIRECTION_EMAIL` (optionnel, par defaut: `dassigbe@gmail.com`)

Note : lors de l'activation d'un etablissement, l'email est envoye a `directorEmail` (champ "Email du directeur" du formulaire).

Exemple (variables d'environnement) :

```bash
set SMTP_HOST=smtp.exemple.com
set SMTP_PORT=587
set SMTP_USERNAME=no-reply@exemple.com
set SMTP_PASSWORD=xxxxxx
set SMTP_STARTTLS=true
set DIRECTION_EMAIL=dassigbe@gmail.com
```
