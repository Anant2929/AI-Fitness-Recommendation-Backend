package com.fitness.orchestrator_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto {
    private String recipientEmail;
    private String subject;
    private String body;
    private String userId;
}
