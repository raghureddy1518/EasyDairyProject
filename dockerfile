# 1️⃣ Use Java 17 (Render supports it well)
FROM eclipse-temurin:17-jdk-alpine

# 2️⃣ Set working directory
WORKDIR /app

# 3️⃣ Copy Maven wrapper & pom first (better caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY .env .env


# 4️⃣ Make mvnw executable
RUN chmod +x mvnw

# 5️⃣ Install dependencies (skip tests)
RUN ./mvnw dependency:go-offline -B

# 6️⃣ Copy source code
COPY src src

# 7️⃣ Build the jar
RUN ./mvnw clean package -DskipTests

# 8️⃣ Run the application
CMD ["java", "-jar", "target/EasyDairy1-0.0.1-SNAPSHOT.jar"]
