package com.fitness.orchestrator_service.service;

import com.fitness.orchestrator_service.client.UserClient;
import com.fitness.orchestrator_service.config.RabbitMQConfig;
import com.fitness.orchestrator_service.dto.AiRecommendationEvent;
import com.fitness.orchestrator_service.dto.EmailDto;
import com.fitness.orchestrator_service.dto.UserDto;
import com.fitness.orchestrator_service.model.Recommendation;
import com.fitness.orchestrator_service.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrchestratorService {

    private final UserClient userClient;
    private final RecommendationRepository repository;
    private final RabbitTemplate rabbitTemplate;

    private static final Set<String> HIGH_IMPACT_ACTIVITIES = Set.of(
            "RUNNING", "JUMPING", "HIIT", "SPRINTING", "BOXING", "CROSSFIT"
    );

    private static final Set<String> HIGH_INTENSITY_ACTIVITIES = Set.of(
            "HIIT", "WEIGHT_LIFTING", "POWER_LIFTING", "SPRINTING"
    );

    public void processRecommendation(AiRecommendationEvent event) {
        log.info("Processing recommendation logic for user: {}", event.getUserId());

        UserDto user = userClient.getUserProfile(event.getUserId());
        log.info("User Stats -> Age: {}, Injury: {}, Fitness: {}", user.getAge(), user.getInjuryStatus(), user.getFitnessLevel());

        String finalAdvice = event.getRecommendation();
        String status = "APPLIED";
        List<String> warnings = new ArrayList<>();
        if (event.getSafety() != null) warnings.addAll(event.getSafety());

        if (hasActiveInjury(user) && isHighImpact(event.getType())) {
            finalAdvice = String.format(
                    "⚠️ SAFETY ALERT: Due to your reported '%s', we strictly advise against %s. " +
                            "AI Recommendation has been overridden. Please switch to Low-Impact activities like Walking, Swimming, or Yoga.",
                    user.getInjuryStatus(), event.getType());

            status = "MODIFIED";
            warnings.add("CRITICAL: High Impact activity detected with active Injury.");
        }

        else if (isSeniorCitizen(user) && isHighIntensity(event.getType())) {
            finalAdvice = String.format(
                    "⚠️ AGE CAUTION: For age %d, %s requires careful heart rate monitoring. " +
                            "Keep intensity moderate. Original AI Advice: %s",
                    user.getAge(), event.getType(), event.getRecommendation());

            status = "MODIFIED";
            warnings.add("AGE WARNING: Monitor Heart Rate closely (Target: <130 BPM).");
        }


        else if ("BEGINNER".equalsIgnoreCase(user.getFitnessLevel()) && isHighIntensity(event.getType())) {
            finalAdvice = "💡 PRO TIP: Since you are a Beginner, start " + event.getType() + " with 50% intensity. " +
                    "Focus on form over speed. " + event.getRecommendation();
            status = "ENHANCED";
        }


        if (user.getWeight() != null && user.getHeight() != null) {
            double bmi = calculateBMI(user.getWeight(), user.getHeight());
            if (bmi > 30.0 && isHighImpact(event.getType())) {
                warnings.add("HEALTH TIP: High impact activities with high BMI can stress joints. Ensure good footwear.");
            }
        }


        saveRecommendation(event, finalAdvice, status, warnings);

        if("MODIFIED".equals(status) || !warnings.isEmpty()){
            sendEmailNotification(user , finalAdvice , warnings);
        }
    }

    private void sendEmailNotification(UserDto user, String advice, List<String> warnings) {
        try {
            String subject = "⚠️ Important Fitness Alert for You!";

            StringBuilder body = new StringBuilder();
            body.append("<h3>Hi " + user.getFirstName() + ",</h3>");
            body.append("<p>We noticed something in your recent activity that needs attention.</p>");
            body.append("<p><b>Advice:</b> " + advice + "</p>");

            if(!warnings.isEmpty()) {
                body.append("<p style='color:red;'><b>Safety Warnings:</b></p><ul>");
                for (String w : warnings) {
                    body.append("<li>" + w + "</li>");
                }
                body.append("</ul>");
            }
            body.append("<br/><p>Stay Safe,<br/>Fitness App Team</p>");

            EmailDto emailDto = new EmailDto(
                    user.getEmail(),
                    subject,
                    body.toString(),
                    user.getId()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY,
                    emailDto
            );

            log.info("📧 Email Event sent to RabbitMQ for User: {}", user.getEmail());

        } catch (Exception e) {
            log.error("Failed to send email event", e);
        }

    }


    private boolean hasActiveInjury(UserDto user) {
        return user.getInjuryStatus() != null && !user.getInjuryStatus().equalsIgnoreCase("NONE");
    }

    private boolean isHighImpact(String activityType) {
        return HIGH_IMPACT_ACTIVITIES.contains(activityType.toUpperCase());
    }

    private boolean isSeniorCitizen(UserDto user) {
        return user.getAge() != null && user.getAge() > 60;
    }

    private boolean isHighIntensity(String activityType) {
        return HIGH_INTENSITY_ACTIVITIES.contains(activityType.toUpperCase());
    }

    private double calculateBMI(Double weightKg, Double heightCm) {
        if (heightCm == 0) return 0;
        double heightM = heightCm / 100;
        return weightKg / (heightM * heightM);
    }

    private void saveRecommendation(AiRecommendationEvent event, String advice, String status, List<String> warnings) {
        Recommendation finalRec = new Recommendation();
        finalRec.setUserId(event.getUserId());
        finalRec.setActivityId(event.getActivityId());
        finalRec.setType(event.getType());
        finalRec.setAiReasoning(event.getRecommendation());
        finalRec.setFinalAction(advice);
        finalRec.setStatus(status);
        finalRec.setSafetyWarnings(warnings);
        finalRec.setCreatedAt(LocalDateTime.now());

        repository.save(finalRec);
        log.info("✅ Recommendation Logic Complete. Status: {}", status);
    }
}