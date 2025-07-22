package com.keylee1110.eventapp_backend.controller;

import com.keylee1110.eventapp_backend.model.Registration;
import com.keylee1110.eventapp_backend.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v0")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    // 1) POST /api/v0/events/{id}/register
    @PostMapping("/events/{id}/register")
    public ResponseEntity<?> registerEvent(@PathVariable("id") String eventId,
                                           Authentication auth) {
        String userId = ((UserDetails)auth.getPrincipal()).getUsername();
        try {
            Registration reg = registrationService.register(userId, eventId);
            return ResponseEntity.ok(reg);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage()); // conflict
        }
    }

    // 2) GET /api/v0/users/{userId}/registrations
    @GetMapping("/users/{userId}/registrations")
    public ResponseEntity<List<Registration>> getUserRegistrations(@PathVariable String userId,
                                                                   Authentication auth) {
        // tuỳ chọn: chỉ cho user tự xem
        String current = ((UserDetails)auth.getPrincipal()).getUsername();
        if (!current.equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        List<Registration> list = registrationService.getByUser(userId);
        return ResponseEntity.ok(list);
    }
}