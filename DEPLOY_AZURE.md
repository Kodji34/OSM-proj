# Déploiement Azure — Guide rapide

Ce guide explique comment configurer GitHub Actions pour déployer automatiquement votre application Spring Boot (JAR) sur Azure App Service et paramétrer l’authentification Microsoft Entra ID.

1) Secrets GitHub requis
- `AZURE_CREDENTIALS` — JSON des credentials du service principal (pour `azure/login`).
- `AZURE_WEBAPP_NAME` — nom de l’App Service (ex: my-app-name).
- `AZURE_RESOURCE_GROUP` — groupe de ressources contenant l’App Service.
- `AZURE_CLIENT_ID`, `AZURE_CLIENT_SECRET`, `AZURE_TENANT_ID` — valeurs de l’App Registration Entra ID (optionnel si vous préférez configurer via portal).

2) Options pour authentifier GitHub Actions
- Option A — Utiliser un **publish profile** (plus simple):
  - Récupérez le publish profile depuis Azure Portal (App Service → Get publish profile) et ajoutez-le en tant que secret `AZURE_PUBLISH_PROFILE`. Vous pouvez ensuite utiliser `azure/webapps-deploy` en indiquant `publish-profile`.
- Option B — Utiliser un **service principal** (recommandé en CI/CD):
  - Créez un SP et récupérez la sortie de `az ad sp create-for-rbac --name "github-sp-<app>" --role contributor --scopes /subscriptions/<SUBSCRIPTION_ID>/resourceGroups/<RG>`
  - Copiez tout le JSON retourné dans le secret GitHub `AZURE_CREDENTIALS`.

3) Vérifications préalables
- Assurez-vous que le JAR est produit par Maven dans `target/` (le workflow exécute `mvn package`).
- Ajoutez les Redirect URIs dans l’enregistrement d’application Entra ID:
  - Local: `http://localhost:8080/login/oauth2/code/azure`
  - Production: `https://<your-app>.azurewebsites.net/login/oauth2/code/azure`

4) Variables d’application (App Settings)
Vous pouvez définir les variables d’environnement dans Azure Portal → App Service → Configuration ou via le workflow (le fichier `azure-webapp-deploy.yml` contient une étape pour le faire si les secrets sont fournis). Les clés typiques pour Spring Boot sont:

- `spring.security.oauth2.client.registration.azure.client-id`
- `spring.security.oauth2.client.registration.azure.client-secret`
- `spring.security.oauth2.client.provider.azure.issuer-uri` = `https://login.microsoftonline.com/<TENANT_ID>/v2.0`

5) Exemple — créer un service principal (exécuter localement après `az login`):
```
az ad sp create-for-rbac --name "github-sp-myapp" --role contributor --scopes /subscriptions/<SUBSCRIPTION_ID>/resourceGroups/<RG>
```

6) Ajouter les secrets dans GitHub
- Repository → Settings → Secrets and variables → Actions → New repository secret

7) Déclencher le déploiement
- Pousser sur `main` déclenchera le workflow: il build le JAR, se connecte à Azure et déploie.

Si vous souhaitez, je peux générer automatiquement le JSON du service principal (requiert que vous ayez `az` et les droits) ou adapter le workflow pour utiliser le `publish profile` à la place du service principal.
