package com.fitness.userservice.controller;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.models.User;
import com.fitness.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId){
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody(required = false) RegisterRequest request,

            @RequestHeader(value = "X-User-ID") String keycloakId,
            @RequestHeader(value = "X-User-Email", required = false) String email,
            @RequestHeader(value = "X-User-First-Name", required = false) String firstName,
            @RequestHeader(value = "X-User-Last-Name", required = false) String lastName)
    {
        if (request == null) {
            request = new RegisterRequest();
        }

        request.setKeycloakId(keycloakId);

        if (request.getEmail() == null) request.setEmail(email);
        if (request.getFirstName() == null) request.setFirstName(firstName);
        if (request.getLastName() == null) request.setLastName(lastName);
        if (request.getPassword() == null) request.setPassword("oauth2-user");
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userId){
        return ResponseEntity.ok(userService.existByUserId(userId));
    }

    @GetMapping("/{userId}/completeness")
    public ResponseEntity<Boolean> checkProfileCompleteness(@PathVariable String userId) {
        return ResponseEntity.ok(userService.isProfileComplete(userId));
    }

    @PutMapping("/{userId}/update-health")
    public ResponseEntity<User> updateHealthData(
            @PathVariable String userId,
            @RequestBody User healthData
    ) {
        User existingUser = userService.updateHealth(userId, healthData);
        return ResponseEntity.ok(existingUser);
    }
}
