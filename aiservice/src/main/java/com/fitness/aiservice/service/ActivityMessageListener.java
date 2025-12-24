package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final ActivityAIService activityAIService;
    private final KafkaTemplate<String , Object> kafkaTemplate;
    private final RecommendationRepository recommendationRepository;

    @Value("${kafka.topic.recommendation-name}")
    private String recommendationTopicName;

    @KafkaListener(topics = "${kafka.topic.name}" , groupId = "activity-processor-group")
    public void processActivity(Activity activity){
        try {
            log.info("Received activity message: {}", activity.getUserId());
            Recommendation recommendation = activityAIService.generateRecommendation(activity);
            recommendationRepository.save(recommendation);

            log.info("Sending Recommendation to Topic: {}", recommendationTopicName);
            kafkaTemplate.send(recommendationTopicName, recommendation);
        } catch (Exception e) {
            log.error("Error processing activity event: {}", e.getMessage());
        }
    }
}
