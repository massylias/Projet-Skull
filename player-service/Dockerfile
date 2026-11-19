# =====================================================
# Dockerfile - PlayerService (Spring Boot)
# Projet Skull - Lot 1
# =====================================================

# ===== Étape 1 : Build =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copier le pom.xml et télécharger les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source et compiler
COPY src ./src
RUN mvn clean package -DskipTests

# ===== Étape 2 : Runtime =====
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Utilisateur non-root
RUN addgroup -S skull && adduser -S skull -G skull
USER skull

# Copier le JAR compilé
COPY --from=build /app/target/*.jar app.jar

# Exposer le port
EXPOSE 8080

# Healthcheck (Actuator)
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Lancer l'application
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]