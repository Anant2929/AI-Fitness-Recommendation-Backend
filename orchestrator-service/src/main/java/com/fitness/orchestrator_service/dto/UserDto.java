package com.fitness.orchestrator_service.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String firstName;
    private String email;

    private Integer age;
    private Double weight;
    private Double height;
    private String injuryStatus;
    private String fitnessLevel;
}
