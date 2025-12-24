package com.fitness.analytics_service.service;


import com.fitness.analytics_service.dto.ActivityEvent;
import com.fitness.analytics_service.model.UserAnalytics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsService {

    private final RedisTemplate<String , Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "analytics:user:";


    public void updateAnalytics(ActivityEvent event){
        String key = KEY_PREFIX + event.getUserId();

        Object result = redisTemplate.opsForValue().get(key);
        UserAnalytics analytics;

        if(result == null){
            analytics = new UserAnalytics(event.getUserId() , 0 , 0.0 , 0.0 , 0 , null);
            log.info("Creating new analytics record for user: {}", event.getUserId());
        } else {
            analytics = objectMapper.convertValue(result, UserAnalytics.class);
        }

        analytics.setTotalCaloriesBurned(analytics.getTotalCaloriesBurned() + event.getCaloriesBurned());

        if(event.getAdditionalMetrics() != null && event.getAdditionalMetrics().containsKey("steps")){
            int steps = Integer.parseInt(event.getAdditionalMetrics().get("steps").toString());
            analytics.setTotalSteps(analytics.getTotalSteps() + steps);
        }

        if (event.getAdditionalMetrics() != null && event.getAdditionalMetrics().containsKey("distanceKm")) {
            double dist = Double.parseDouble(event.getAdditionalMetrics().get("distanceKm").toString());
            analytics.setTotalDistanceKm(analytics.getTotalDistanceKm() + dist);
        }

        LocalDate today = LocalDate.now();
        LocalDate lastDate = analytics.getLastActiveDate();

        if (lastDate == null) {
            analytics.setCurrentStreak(1);
        } else if (lastDate.equals(today.minusDays(1))) {
            analytics.setCurrentStreak(analytics.getCurrentStreak() + 1);
        } else if (lastDate.isBefore(today.minusDays(1))) {
            analytics.setCurrentStreak(1);
        }

        analytics.setLastActiveDate(today);
        redisTemplate.opsForValue().set(key , analytics);
        log.info("Redis Updated for {}: Steps={}, Calories={}, Streak={}",
                event.getUserId(), analytics.getTotalSteps(), analytics.getTotalCaloriesBurned(), analytics.getCurrentStreak());
    }

    public UserAnalytics getUserAnalytics(String userId) {
        String key = KEY_PREFIX + userId;
        Object result = redisTemplate.opsForValue().get(key);

        if (result != null) {
            return objectMapper.convertValue(result, UserAnalytics.class);
        }


        return new UserAnalytics(userId, 0, 0.0, 0.0, 0, null);
    }

}
