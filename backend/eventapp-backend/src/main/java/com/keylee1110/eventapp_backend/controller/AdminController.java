package com.keylee1110.eventapp_backend.controller;

import com.keylee1110.eventapp_backend.model.User;
import com.keylee1110.eventapp_backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v0/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Endpoint to update user roles
    @PostMapping("/users/{username}/roles")
    @PreAuthorize("hasRole('ADMIN')") // Only users with ROLE_ADMIN can access this
    public ResponseEntity<?> updateUserRoles(@PathVariable String username, @RequestBody List<String> roles) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User is not found."));

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User roles updated successfully!"));
    }
}
