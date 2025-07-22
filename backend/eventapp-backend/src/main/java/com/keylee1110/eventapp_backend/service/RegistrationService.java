package com.keylee1110.eventapp_backend.service;

import com.keylee1110.eventapp_backend.model.Registration;

import java.util.List;

public interface RegistrationService {
    Registration register(String userId, String eventId);
    List<Registration> getByUser(String userId);
}