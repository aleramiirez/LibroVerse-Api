# Paso 1: Build con Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copiar el pom y el código
COPY pom.xml .
COPY src ./src
# Compilar saltando los tests para ir más rápido en el despliegue
RUN mvn clean package -DskipTests

# Paso 2: Imagen de ejecución ligera
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copiar solo el archivo JAR generado en el paso anterior
COPY --from=build /app/target/*.jar app.jar
# Exponer el puerto que usa la app
EXPOSE 8080
# Comando para arrancar
ENTRYPOINT ["java", "-jar", "app.jar"]
