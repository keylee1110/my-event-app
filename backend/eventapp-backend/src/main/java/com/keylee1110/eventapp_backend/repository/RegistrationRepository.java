package com.keylee1110.eventapp_backend.repository;

import com.keylee1110.eventapp_backend.model.Registration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends MongoRepository<Registration, String> {
    List<Registration> findByUserId(String userId);

    long countByEventId(String eventId);

    boolean existsByUserIdAndEventId(String userId, String eventId);
}