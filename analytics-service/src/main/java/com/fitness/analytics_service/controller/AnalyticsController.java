package com.fitness.analytics_service.controller;

import com.fitness.analytics_service.model.UserAnalytics;
import com.fitness.analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserAnalytics> getUserDashboard(@PathVariable String userId) {
        UserAnalytics data = analyticsService.getUserAnalytics(userId);
        return ResponseEntity.ok(data);
    }
}