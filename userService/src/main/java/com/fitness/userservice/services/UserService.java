package com.fitness.userservice.services;

import com.fitness.userservice.UserRepository;
import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.models.User;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository repository;

    public @Nullable UserResponse register(RegisterRequest request) {

        if(repository.existsByEmail(request.getEmail())){
            User savedUser = repository.findByEmail(request.getEmail());
            UserResponse userResponse = new UserResponse();
            userResponse.setId(String.valueOf(savedUser.getId()));
            userResponse.setEmail(savedUser.getEmail());
            userResponse.setPassword(savedUser.getPassword());
            userResponse.setFirstName(savedUser.getFirstName());
            userResponse.setLastName(savedUser.getLastName());
            userResponse.setCreatedAt(savedUser.getCreatedAt());
            userResponse.setUpdatedAt(savedUser.getUpdatedAt());
            userResponse.setAge(savedUser.getAge());
            userResponse.setWeight(savedUser.getWeight());
            userResponse.setHeight(savedUser.getHeight());
            userResponse.setInjuryStatus(savedUser.getInjuryStatus());
            userResponse.setFitnessLevel(savedUser.getFitnessLevel());
            return userResponse;
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setKeycloakId(request.getKeycloakId());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = repository.save(user);
        UserResponse userResponse = new UserResponse();

        try {
            userResponse.setId(String.valueOf(savedUser.getId()));
            userResponse.setEmail(savedUser.getEmail());
            userResponse.setPassword(savedUser.getPassword());
            userResponse.setFirstName(savedUser.getFirstName());
            userResponse.setLastName(savedUser.getLastName());
            userResponse.setCreatedAt(savedUser.getCreatedAt());
            userResponse.setUpdatedAt(savedUser.getUpdatedAt());
            return userResponse;
        } catch (Exception e) {
            System.out.println("Error occurred while saving user: " + e.getMessage());
            return null;
        }
    }

    public @Nullable UserResponse getUserProfile(String userId) {
        User user = repository.findByKeycloakId(userId);
        if (user == null) {
            System.out.println("User not found with ID: " + userId);
            return null;
        }

        UserResponse userResponse = new UserResponse();
        try {
            userResponse.setId(String.valueOf(user.getId()));
            userResponse.setEmail(user.getEmail());
            userResponse.setPassword(user.getPassword());
            userResponse.setFirstName(user.getFirstName());
            userResponse.setLastName(user.getLastName());
            userResponse.setAge(user.getAge());
            userResponse.setWeight(user.getWeight());
            userResponse.setHeight(user.getHeight());
            userResponse.setInjuryStatus(user.getInjuryStatus());
            userResponse.setFitnessLevel(user.getFitnessLevel());
            userResponse.setCreatedAt(user.getCreatedAt());
            userResponse.setUpdatedAt(user.getUpdatedAt());
            return userResponse;
        } catch (Exception e) {
            System.out.println("Error occurred while retrieving user: " + e.getMessage());
            return null;
        }
    }

    public boolean isProfileComplete(String userId) {
        User user = repository.findByKeycloakId(userId);

        if (user == null) return false;
        return user.getAge() != null &&
                user.getWeight() != null &&
                user.getHeight() != null;
    }

    public @Nullable Boolean existByUserId(String userId) {
        return repository.existsByKeycloakId(userId);
    }

    public User updateHealth(String userId, User healthData) {
        User existingUser = repository.findByKeycloakId(userId);
        if (existingUser == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        if (healthData.getAge() != null) {
            existingUser.setAge(healthData.getAge());
        }
        if (healthData.getWeight() != null) {
            existingUser.setWeight(healthData.getWeight());
        }
        if (healthData.getHeight() != null) {
            existingUser.setHeight(healthData.getHeight());
        }
        if (healthData.getInjuryStatus() != null) {
            existingUser.setInjuryStatus(healthData.getInjuryStatus());
        }
        if (healthData.getFitnessLevel() != null) {
            existingUser.setFitnessLevel(healthData.getFitnessLevel());
        }

        return repository.save(existingUser);
    }
}
