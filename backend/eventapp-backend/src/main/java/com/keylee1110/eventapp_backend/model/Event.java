package com.keylee1110.eventapp_backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Document(collection = "events")
public class Event {
    @Id
    private String id;
    private String title;
    private String description;
    private LocalDate date;
    private String location;
    private Integer capacity;
    private String createdBy;

    public Event(String title, String description, LocalDate date,
                 String location, Integer capacity, String createdBy) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.capacity = capacity;
        this.createdBy = createdBy;
    }
}
