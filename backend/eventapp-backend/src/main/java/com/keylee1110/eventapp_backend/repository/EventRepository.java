package com.keylee1110.eventapp_backend.repository;

import com.keylee1110.eventapp_backend.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByCreatedBy(String username);
}
