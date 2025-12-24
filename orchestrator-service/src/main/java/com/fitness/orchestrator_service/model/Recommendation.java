package com.fitness.orchestrator_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "final_recommendations")
public class Recommendation {
    @Id
    private String id;

    private String userId;
    private String activityId;
    private String type;

    private String aiReasoning;

    private String finalAction;
    private String status;
    private List<String> safetyWarnings;

    @CreatedDate
    private LocalDateTime createdAt;
}
