package com.ecomarket.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecomarket.auth.dto.UserProfileResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.ecomarket.auth.User user = userRepository.findByUsername(username).orElseGet(() -> userRepository.findByEmail(username).orElse(null));
        if (user == null) return ResponseEntity.notFound().build();
        UserProfileResponse resp = new UserProfileResponse(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt(), user.getUpdatedAt());
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMe(@Valid @RequestBody UserProfileResponse update) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.ecomarket.auth.User user = userRepository.findByUsername(username).orElseGet(() -> userRepository.findByEmail(username).orElse(null));
        if (user == null) return ResponseEntity.notFound().build();
        if (update.getEmail() != null) user.setEmail(update.getEmail());
        // Do not update username here to keep simple
        com.ecomarket.auth.User saved = userRepository.save(user);
        UserProfileResponse resp = new UserProfileResponse(saved.getId(), saved.getUsername(), saved.getEmail(), saved.getCreatedAt(), saved.getUpdatedAt());
        return ResponseEntity.ok(resp);
    }
}
