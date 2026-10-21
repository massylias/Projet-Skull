#  Projet Skull - Application Web
> *Projet informatique  - Université d'Orléans - M1 MIAGE*
---

Bienvenue sur le dépôt officiel du projet **Skull** réalisé dans le cadre de la M1 MIAGE (2026/2027).

Ce projet est une application web complète permettant de jouer au jeu de bluff et d'enchères "Skull" en ligne, en compétition avec d'autres joueurs ou des intelligences artificielles.

##  Équipe

- **Massylias SLIMANI**
- **Ibrahim Nemili-CHIBANI**
- **Fatoumata Binta DIALLO**
- **Christian BITODI**
- **Thibaut DE FREITAS**

##  Architecture Technique

- **Back-end** : Java 17, Spring Boot, Spring Security (à venir)
- **Base de données** : MariaDB 10.11
- **Déploiement** : Docker, Docker Compose
- **Documentation API** : Swagger / OpenAPI (Springdoc)
- **Outils** : IntelliJ IDEA, Git

##  Démarrage Rapide

Pour lancer l'infrastructure locale (Base de données + Application) , consultez le guide dédié :

- **[Guide de démarrage Docker](docs/README.md)**

##  Identifiants de test (Environnement local)

Pour faciliter les tests, voici les identifiants configurés dans l'environnement Docker local.

### Base de données (MariaDB)
- **Hôte** : `localhost`
- **Port** : `3306`
- **Base de données** : `skull`
- **Utilisateur** : `skull`
- **Mot de passe** : `skull`


##  Documentation du Projet

- [Guide Docker](docs/README.md)
- [Modélisation (MCD, MLD, Script SQL de référence)](docs/)


##  Structure des Lots

- **Lot 1** : Architecture et Interfaces des services, PlayerService (Inscription / Connexion / Compte Administrateur), Persistance des donneés et conteneurisation de base Docker.


- **Lot 2** : Spring Security et JWT, GameService (création, jonction et sortie de partie, règles de Skull, IA simple), LadderService (historique et classement), signalement et bannissement.


- **Lot 3** : MarketService (achat d'apparences par opération mathématique), Interface complète, spécification OpenAPI,  environnement Docker complet, Rapport final.

---
##  Dépendances du Projet

Le projet utilise **Spring Boot 4.1.1** avec les dépendances Maven suivantes, configurées dans le fichier `pom.xml` :

###  Spring Web (API REST)

- **Artifact** : `spring-boot-starter-web`
- **Rôle** : Permet de créer des contrôleurs REST (`@RestController`, `@GetMapping`, `@PostMapping`) et de gérer les requêtes/réponses HTTP en JSON.

###  Spring Data JPA (Accès Base de Données)
- **Artifact** : `spring-boot-starter-data-jpa`
- **Rôle** : Fournit une couche d'abstraction (via Hibernate). Génère automatiquement les requêtes SQL à partir des noms de méthodes . Permet la création automatique des tables au démarrage (`ddl-auto=update`).

###  MariaDB Driver 
- **Artifact** : `mariadb-java-client`
- **Rôle** : Pilote officiel pour connecter l'application Spring Boot à la base de données MariaDB.

###  Spring Boot Validation
- **Artifact** : `spring-boot-starter-validation`
- **Rôle** : Permet de valider les données reçues dans les requêtes HTTP (format de l'email, champs obligatoires, taille du mot de passe) via les annotations `@Valid`, `@NotBlank`, `@Email`, `@Size`.

###  Spring Security Crypto
- **Artifact** : `spring-security-crypto`
- **Rôle** : Fournit l'algorithme de hachage **BCrypt** pour sécuriser les mots de passe. *(Spring Security complet sera ajouté au Lot 2 pour l'authentification JWT).*

###  Springdoc OpenAPI (Swagger UI)
- **Artifact** : `springdoc-openapi-starter-webmvc-ui` (v2.8.5)
- **Rôle** : Génère automatiquement une documentation interactive de l'API (Swagger UI) à partir des contrôleurs REST. Accessible sur `/swagger-ui/index.html`.

###  Spring Boot Actuator
- **Artifact** : `spring-boot-starter-actuator`
- **Rôle** : Ajoute des endpoints de monitoring (santé, métriques) pour vérifier l'état de l'application et de la base de données. Utilisé par Docker pour le healthcheck (`/actuator/health`).

###  Spring Boot DevTools
- **Artifact** : `spring-boot-devtools`
- **Rôle** : Active le rechargement automatique de l'application lors des modifications de code (uniquement en développement).

###  Spring Boot Starter Test
- **Artifact** : `spring-boot-starter-test`
- **Rôle** : Fournit les outils de test (JUnit 5, Mockito, MockMvc, AssertJ) pour les tests unitaires et d'intégration.