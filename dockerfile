# 1️⃣ Use Java 17 (Render supports it well)
FROM eclipse-temurin:17-jdk-alpine

# 2️⃣ Set working directory
WORKDIR /app

# 3️⃣ Copy Maven wrapper & pom first (better caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# 4️⃣ Install dependencies (skip tests)
RUN ./mvnw dependency:go-offline -B

# 5️⃣ Copy source code
COPY src src

# 6️⃣ Build the jar
RUN ./mvnw clean package -DskipTests

# 7️⃣ Run the application
CMD ["java", "-jar", "target/EasyDairy1-0.0.1-SNAPSHOT.jar"]
