package com.fitness.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final WebClient userServiceWebClient;

    // Gateway -> UserService.java

    public Mono<Boolean> validateUser(String userId, String token) { // 1. Token accept karo
        log.info("Calling User Service for validation: {}", userId);

        return userServiceWebClient.get()
                .uri("/api/users/{userId}/validate", userId)
                .header("Authorization", token) // 2. Yaha Token Chipkao!
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    // 3. Debugging ke liye Asli Error Log karo
                    log.error("Error from User Service. Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());

                    if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                        return Mono.error(new RuntimeException("User not found : " + userId));

                    else if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                        return Mono.error(new RuntimeException("Invalid : " + userId));

                    // Agar 401 ya 500 aaya to yaha girega
                    return Mono.error(new RuntimeException("Unexpected error (" + e.getStatusCode() + ") : " + userId));
                });
    }

    public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {
        log.info("Calling User Registration for {}", registerRequest.getEmail());
        return userServiceWebClient.post()
                .uri("/api/users/register")
                .header("X-User-ID", registerRequest.getKeycloakId())
                .header("X-User-Email", registerRequest.getEmail())
                .header("X-User-First-Name", registerRequest.getFirstName())
                .header("X-User-Last-Name", registerRequest.getLastName())
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                        return Mono.error(new RuntimeException("Bad request : " + e.getMessage()));

                    return Mono.error(new RuntimeException("Unexpected error : " + e.getMessage()));
                });
    }

    public Mono<Boolean> checkProfileCompleteness(String userId) {
        return userServiceWebClient.get()
                .uri("/api/users/{userId}/completeness", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                // Error handling agar user na mile
                .onErrorReturn(false);
    }
}