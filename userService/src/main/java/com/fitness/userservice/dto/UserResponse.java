package com.fitness.userservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class  UserResponse {

    private String id;
    private String keycloakId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Integer age;
    private Double weight;
    private Double height;
    private String injuryStatus = "NONE";
    private String fitnessLevel = "BEGINNER";
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
