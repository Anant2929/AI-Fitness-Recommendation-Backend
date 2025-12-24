package com.fitness.orchestrator_service.client;

import com.fitness.orchestrator_service.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    /// Make sure user controller is returning age,weight etc.
    @GetMapping("/api/users/{userId}")
    UserDto getUserProfile(@PathVariable("userId") String userId);
}
