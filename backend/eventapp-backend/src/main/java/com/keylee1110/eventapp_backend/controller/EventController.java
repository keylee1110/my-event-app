package com.keylee1110.eventapp_backend.controller;

import com.keylee1110.eventapp_backend.model.Event;
import com.keylee1110.eventapp_backend.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;


@RestController
@RequestMapping("/api/v0/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event newEvent,
                                             Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        newEvent.setCreatedBy(username);
        Event saved = eventService.create(newEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> list = eventService.findAll();
        return ResponseEntity.ok(list);
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable String id) {
        return eventService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Event with id " + id + " not found"));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable String id,
                                         @RequestBody Event updatedEvent,
                                         Authentication authentication) {
        // (tuỳ chọn) kiểm tra quyền sở hữu: chỉ creator mới update được
        String currentUser = ((UserDetails) authentication.getPrincipal()).getUsername();
        return eventService.findById(id)
                .map(existing -> {
                    if (!existing.getCreatedBy().equals(currentUser)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Not allowed to modify this event");
                    }
                    updatedEvent.setId(id);
                    updatedEvent.setCreatedBy(currentUser);
                    Event saved = eventService.update(id, updatedEvent);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Event with id " + id + " not found"));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable String id,
                                         Authentication authentication) {
        String currentUser = ((UserDetails) authentication.getPrincipal()).getUsername();
        return eventService.findById(id)
                .map(existing -> {
                    if (!existing.getCreatedBy().equals(currentUser)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Not allowed to delete this event");
                    }
                    eventService.delete(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Event with id " + id + " not found"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyDetails(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Map<String, Object> info = new HashMap<>();
        info.put("username", userDetails.getUsername());
        info.put("authorities", userDetails.getAuthorities());
        return ResponseEntity.ok(info);
    }
}
