package com.keylee1110.eventapp_backend.service;

import com.keylee1110.eventapp_backend.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventService {
    Event create(Event event);
    List<Event> findAll();
    Optional<Event> findById(String id);
    Event update(String id, Event event);
    void delete(String id);
}