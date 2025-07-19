package com.keylee1110.eventapp_backend.service;

import com.keylee1110.eventapp_backend.model.Event;
import com.keylee1110.eventapp_backend.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository repo;

    public EventServiceImpl(EventRepository repo) {
        this.repo = repo;
    }

    @Override
    public Event create(Event event) {
        return repo.save(event);
    }

    @Override
    public List<Event> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Event> findById(String id) {
        return repo.findById(id);
    }

    @Override
    public Event update(String id, Event event) {
        event.setId(id);
        return repo.save(event);
    }

    @Override
    public void delete(String id) {
        repo.deleteById(id);
    }
}
