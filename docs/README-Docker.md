#  Guide Docker - Projet Skull

Ce guide explique comment lancer l'environnement de développement.

##  1. Prérequis

- IntelliJ IDEA (JDK 17)

##  2. Identifiants configurés
Lorsque vous lancez Docker, les identifiants suivants sont créés automatiquement (définis dans le `docker-compose.yaml`) :

### MariaDB (Base de données)
| Paramètre | Valeur |
|-----------|--------|
| **Hôte** | `localhost` (ou `mariadb` depuis un autre conteneur) |
| **Port** | `3306` |
| **Base** | `skull` |
| **Utilisateur** | `skull` |
| **Mot de passe** | `skull` |


##  3. Lancer le projet

Au démarrage 
```bash
docker compose up -d --build  
```
## 4. Vérifier la connexion

- Test Spring Boot :
``` bash
http://localhost:8080/actuator/health 
```
(doit afficher "db": {"status": "UP"})


- Test MariaDB : (pour vérifier les tables )
```bash
docker exec -it skull-mariadb mariadb -u skull -pskull skull -e "SHOW TABLES;"
```


## 5. Arrêter le projet  
``` bash
docker compose down     # Garde les données
docker compose down -v   # Supprime les données (réinitialisation)
```
