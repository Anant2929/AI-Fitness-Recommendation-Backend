package com.fitness.orchestrator_service.controller;

import com.fitness.orchestrator_service.model.Recommendation;
import com.fitness.orchestrator_service.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finalRecommendations")
@RequiredArgsConstructor
@Slf4j
public class OrchestratorController {

    private final RecommendationRepository repository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recommendation>> getUserRecommendations(@PathVariable String userId) {
        log.info("Calling /api/finalRecommendations/user/{} endpoint", userId);
        return ResponseEntity.ok(repository.findByUserId(userId));
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Recommendation> getActivityRecommendation(@PathVariable String activityId) {
        log.info("Calling /api/finalRecommendations/activity/{} endpoint", activityId);
        return ResponseEntity.ok(repository.findByActivityId(activityId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found")));
    }
}