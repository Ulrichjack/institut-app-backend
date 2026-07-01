
# ─── ÉTAPE 1: BUILD (Compilation isolée) ───
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Optimisation du cache : on télécharge les dépendances d'abord
COPY pom.xml .
RUN mvn dependency:go-offline -B

# On copie le code et on package
COPY src ./src
RUN mvn clean package -DskipTests

# ─── ÉTAPE 2: IMAGE FINALE (Ultra-légère et sécurisée) ───
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# NOUVEAU : Obligatoire pour ton PostgresBackupService (pg_dump)
RUN apk add --no-cache postgresql-client

# Récupère le JAR de l'étape 1
COPY --from=build /app/target/*.jar app.jar

# Variables d'environnement par défaut
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

# Port interne de référence pour le conteneur
EXPOSE 8080

# Lancement compatible Cloud ($PORT) et Local (8080 par défaut)
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar app.jar --server.port=${PORT:-8080}"]


## Étape 1: Build avec Maven
#FROM maven:3.8.4-openjdk-17-slim AS build
#WORKDIR /app
#
## Copie les fichiers de configuration Maven
#COPY pom.xml .
#COPY src ./src
#
## Compile et package l'application (sans les tests pour aller plus vite)
#RUN mvn clean package -DskipTests
#
## Étape 2: Image finale légère avec seulement le JAR
#FROM eclipse-temurin:17-jdk-focal
#WORKDIR /app
#
## Copie le JAR compilé depuis l'étape de build
#COPY --from=build /app/target/*.jar app.jar
#
## Expose le port 8081
#EXPOSE $PORT
#
## Variables d'environnement pour optimiser la JVM
#ENV JAVA_OPTS="-Xmx512m -Xms256m"
#
#
#ENV LANG=C.UTF-8
#ENV LC_ALL=C.UTF-8
## Commande pour lancer l'application
##ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
#
## Commande pour lancer l'application avec le profil de production par défaut
## Ou en utilisant la variable d'environnement $PORT de Render
#ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar app.jar --server.port=${PORT:-8080}"]


