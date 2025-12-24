package com.fitness.orchestrator_service.kafka;

import com.fitness.orchestrator_service.dto.AiRecommendationEvent;
import com.fitness.orchestrator_service.service.OrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationConsumer {

    private final OrchestratorService orchestratorService;

    @KafkaListener(topics = "ai-recommendation-topic", groupId = "orchestrator-group-v2")
    public void consumeAiAdvice(AiRecommendationEvent event) {
        log.info("Kafka Message Received for User: {}", event.getUserId());
        orchestratorService.processRecommendation(event);
    }


}
