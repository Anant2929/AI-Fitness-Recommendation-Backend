package com.fitness.activityservice.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigPrinter {

    @Value("${spring.data.mongodb.uri:Not Found}")
    private String mongoUri;

    @Value("${spring.kafka.bootstrap-servers:Not Found}")
    private String kafkaServer;

    @PostConstruct
    public void printConfig() {
        System.out.println("=========================================");
        System.out.println("🔎 DEBUGGING CONFIGURATION 🔎");
        System.out.println("👉 MongoDB URI: " + mongoUri);
        System.out.println("👉 Kafka Server: " + kafkaServer);
        System.out.println("=========================================");
    }
}