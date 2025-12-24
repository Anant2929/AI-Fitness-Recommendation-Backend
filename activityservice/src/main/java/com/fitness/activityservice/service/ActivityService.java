package com.fitness.activityservice.service;


import com.fitness.activityservice.ActivityRepository;
import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final KafkaTemplate<String , Activity> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;

    public ActivityResponse trackActivity(ActivityRequest activityRequest) {

        Activity activity = Activity.builder()
                .userId(activityRequest.getUserId())
                .type(activityRequest.getType())
                .duration(activityRequest.getDuration())
                .caloriesBurned(activityRequest.getCaloriesBurned())
                .startTime(activityRequest.getStartTime())
                .additionalMetrics(activityRequest.getAdditionalMetrics())
                .build();

        // 🔍 DEBUG LOG 1: Check Data before saving
        System.out.println("==========================================");
        System.out.println("🕵️‍♂️ DEBUG: Attempting to save Activity to MongoDB");
        System.out.println("📦 DATA: " + activity.toString());
        System.out.println("==========================================");

        Activity savedActivity;
        try {
            
            savedActivity = activityRepository.save(activity);

            
            System.out.println("✅ DEBUG: Successfully Saved! ID: " + savedActivity.getId());

        } catch (Exception e) {
            System.out.println("❌ DEBUG: MongoDB Save Failed!");
            System.out.println("💥 ERROR MESSAGE: " + e.getMessage());
            e.printStackTrace(); 
            throw new RuntimeException("Database Save Failed: " + e.getMessage());
        }

        
        try {
            System.out.println("📨 DEBUG: Sending to Kafka Topic: " + topicName);
            kafkaTemplate.send(topicName , savedActivity.getUserId() , savedActivity);
            System.out.println("✅ DEBUG: Sent to Kafka successfully");
        } catch (Exception e) {
            System.out.println("❌ DEBUG: Kafka Send Failed: " + e.getMessage());
        }

        return mapToResponse(savedActivity);
    }

    private @Nullable ActivityResponse mapToResponse(Activity savedActivity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(savedActivity.getId());
        response.setUserId(savedActivity.getUserId());
        response.setType(savedActivity.getType());
        response.setDuration(savedActivity.getDuration());
        response.setCaloriesBurned(savedActivity.getCaloriesBurned());
        response.setStartTime(savedActivity.getStartTime());
        response.setAdditionalMetrics(savedActivity.getAdditionalMetrics());
        response.setCreatedAt(savedActivity.getCreatedAt());
        response.setUpdatedAt(savedActivity.getUpdatedAt());
        return response;
    }
}
