package com.fitness.orchestrator_service.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class SimpleMongoConfig {

    // 🔴 HARDCODED URI (Same as Activity Service)
    private static final String CONNECTION_STRING = "mongodb://mongo:27017/orchestrator_db";

    @Bean
    public MongoClient mongoClient() {
        System.out.println("🔥 Orchestrator-SERVICE FORCE-CONNECTING TO MONGO URI: " + CONNECTION_STRING);
        return MongoClients.create(CONNECTION_STRING);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "orchestrator_db");
    }
}