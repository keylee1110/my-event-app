package com.keylee1110.eventapp_backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "registrations")
public class Registration {
    @Id
    private String id;
    private String userId;
    private String eventId;
    private Instant timestamp;

    public Registration(String userId, String eventId, Instant timestamp) {
        this.userId = userId;
        this.eventId = eventId;
        this.timestamp = timestamp;
    }

}