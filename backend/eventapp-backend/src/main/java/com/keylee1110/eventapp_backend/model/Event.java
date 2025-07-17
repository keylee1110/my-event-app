package com.keylee1110.eventapp_backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "events")
public class Event {
    @Id
    private String id;
    private String name;
    private String description;
    private String createdBy; // To store the username of the creator

    public Event(String name, String description, String createdBy) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
    }
}
