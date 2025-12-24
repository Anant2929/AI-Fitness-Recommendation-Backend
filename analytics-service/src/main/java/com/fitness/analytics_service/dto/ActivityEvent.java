package com.fitness.analytics_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityEvent {
    private String userId;
    private String type;
    private int duration;
    private double caloriesBurned;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics; // Steps, HeartRate etc.
}