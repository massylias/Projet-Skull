# Test de persistance MariaDB (Lot 1)

- Date : 25/10/2026
- Environnement : Docker Desktop, `docker-compose.yaml` du projet, volume `projet-skull_mariadb-data`

## Contexte
Test réalisé directement en base, car l'entité `Player` n'était pas encore disponible dans le code. Il sera refait avec un vrai joueur dès que l'inscription sera disponible.

## Procédure
1. `docker compose up -d` : les conteneurs sont `healthy`.
2. Création de la table `test_persistance` et insertion d'une ligne.
3. `docker compose down` (sans `-v`), puis `docker compose up -d`.
4. Lecture de la table après redémarrage.

## Résultat avant redémarrage
```
MariaDB [skull]> CREATE TABLE test_persistance (id INT PRIMARY KEY AUTO_INCREMENT, message VARCHAR(100));
Query OK, 0 rows affected (0.050 sec)

MariaDB [skull]> INSERT INTO test_persistance (message) VALUES ('avant redemarrage');
Query OK, 1 row affected (0.013 sec)

MariaDB [skull]> SELECT * FROM test_persistance;
+----+-------------------+
| id | message           |
+----+-------------------+
|  1 | avant redemarrage |
+----+-------------------+
1 row in set (0.003 sec)

MariaDB [skull]> EXIT;

```

## Arrêt et redémarrage
```
PS C:\Users\fbd15\IdeaProjects\Projet-Skull> docker compose down
[+] down 3/3
 ✔ Container skull-player-service     Removed                                   0.9s
 ✔ Container skull-mariadb            Removed                                   0.6s
 ✔ Network projet-skull_skull-network Removed                                   0.3s
PS C:\Users\fbd15\IdeaProjects\Projet-Skull> docker compose ps
NAME      IMAGE     COMMAND   SERVICE   CREATED   STATUS    PORTS
PS C:\Users\fbd15\IdeaProjects\Projet-Skull> docker compose up -d
[+] up 3/3
 ✔ Network projet-skull_skull-network Created                                   0.1s
 ✔ Container skull-mariadb            Healthy                                  11.2s
 ✔ Container skull-player-service     Started                                  11.4s
PS C:\Users\fbd15\IdeaProjects\Projet-Skull> docker compose ps
NAME                   IMAGE                         COMMAND                  SERVICE          CREATED          STATUS                            PORTS
skull-mariadb          mariadb:10.11                 "docker-entrypoint.s…"   mariadb          18 seconds ago   Up 17 seconds (healthy)           0.0.0.0:3306->3306/tcp, [::]:3306->3306/tcp
skull-player-service   projet-skull-player-service   "java -Xmx512m -jar …"   player-service   18 seconds ago   Up 6 seconds (health: starting)   0.0.0.0:8080->8080/tcp, [::]:8080->8080/tcp
```

## Résultat après redémarrage
```
MariaDB [(none)]> USE skull;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
MariaDB [skull]> SELECT * FROM test_persistance;
+----+-------------------+
| id | message           |
+----+-------------------+
|  1 | avant redemarrage |
+----+-------------------+
1 row in set (0.005 sec)

MariaDB [skull]> DROP TABLE test_persistance;
Query OK, 0 rows affected (0.026 sec)

MariaDB [skull]> EXIT;
```

## Conclusion
La ligne insérée avant l'arrêt des conteneurs est toujours présente après le redémarrage : les données sont conservées grâce au volume Docker `mariadb-data`. Test validé.