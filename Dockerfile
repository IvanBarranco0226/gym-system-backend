# ETAPA 1: Construcción (Empaqueta la aplicación)
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app
# Copiamos solo el pom.xml primero para aprovechar la caché de Docker
COPY pom.xml .
# Descargamos las dependencias (esto acelera futuras construcciones)
RUN mvn dependency:go-offline
# Ahora copiamos el código fuente y compilamos
COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución (La imagen final que irá a producción)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copiamos SOLO el archivo .jar de la Etapa 1
COPY --from=builder /app/target/*.jar app.jar
# Exponemos el puerto de Spring Boot
EXPOSE 8080
# Comando para arrancar el sistema
ENTRYPOINT ["java", "-jar", "app.jar"]