package com.example.demo;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EasyDairy1Application {

    public static void main(String[] args) {

        // Check if running locally (Render sets "RENDER" env variable in production)
        if (System.getenv("RENDER") == null) {
            // Load environment variables from .env file (local only)
            Dotenv dotenv = Dotenv.load();

            // Set them as System properties so Spring can use ${VAR_NAME}
            dotenv.entries().forEach(entry ->
                    System.setProperty(entry.getKey(), entry.getValue())
            );
        }

        SpringApplication.run(EasyDairy1Application.class, args);
    }
}
