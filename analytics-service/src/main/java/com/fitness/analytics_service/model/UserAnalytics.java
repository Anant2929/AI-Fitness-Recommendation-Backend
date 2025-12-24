package com.fitness.analytics_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAnalytics implements Serializable {
    private String userId;
    private int totalSteps;
    private double totalCaloriesBurned;
    private double totalDistanceKm;
    private int currentStreak;
    private LocalDate lastActiveDate;
}