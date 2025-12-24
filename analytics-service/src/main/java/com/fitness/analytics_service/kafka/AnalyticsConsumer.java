package com.fitness.analytics_service.kafka;


import com.fitness.analytics_service.dto.ActivityEvent;
import com.fitness.analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AnalyticsConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(topics = "activity-events" , groupId = "analytics-group-v2")
    public void consumeActivityEvent(ActivityEvent event){
        log.info("📊 Analytics received activity for user: {}", event.getUserId());
        analyticsService.updateAnalytics(event);
    }
}
