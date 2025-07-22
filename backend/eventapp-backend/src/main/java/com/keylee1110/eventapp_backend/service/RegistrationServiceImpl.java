package com.keylee1110.eventapp_backend.service;

import com.keylee1110.eventapp_backend.model.Event;
import com.keylee1110.eventapp_backend.model.Registration;
import com.keylee1110.eventapp_backend.repository.EventRepository;
import com.keylee1110.eventapp_backend.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepo;
    private final EventRepository eventRepo;

    public RegistrationServiceImpl(RegistrationRepository registrationRepo,
                                   EventRepository eventRepo) {
        this.registrationRepo = registrationRepo;
        this.eventRepo = eventRepo;
    }

    @Override
    public Registration register(String userId, String eventId) {
        // 1. kiểm tra duplicate
        if (registrationRepo.existsByUserIdAndEventId(userId, eventId)) {
            throw new IllegalStateException("User already registered");
        }
        // 2. kiểm tra capacity
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        long registeredCount = registrationRepo.countByEventId(eventId);
        if (registeredCount >= event.getCapacity()) {
            throw new IllegalStateException("Event is full");
        }
        // 3. tạo registration
        Registration reg = new Registration(userId, eventId, Instant.now());
        return registrationRepo.save(reg);
    }

    @Override
    public List<Registration> getByUser(String userId) {
        return registrationRepo.findByUserId(userId);
    }
}
