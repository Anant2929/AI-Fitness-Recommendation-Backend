package com.fitness.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmailDto {
    private String recipientEmail;
    private String subject;
    private String body;
    private String userId;
}
