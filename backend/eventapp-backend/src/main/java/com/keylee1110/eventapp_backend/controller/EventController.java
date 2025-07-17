package com.keylee1110.eventapp_backend.controller;

import com.keylee1110.eventapp_backend.model.Event;
import com.keylee1110.eventapp_backend.repository.EventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/api/v0/events")
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event newEvent, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        newEvent.setCreatedBy(username);
        Event savedEvent = eventRepository.save(newEvent);
        return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Event>> getEvents() {
        List<Event> events = eventRepository.findAll();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyDetails(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Map<String, Object> userDetailsMap = new java.util.HashMap<>();
        userDetailsMap.put("username", userDetails.getUsername());
        userDetailsMap.put("authorities", userDetails.getAuthorities());
        return ResponseEntity.ok(userDetailsMap);
    }
}
