package com.keylee1110.eventapp_backend.service;

import com.keylee1110.eventapp_backend.model.Event;
import com.keylee1110.eventapp_backend.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock
    private EventRepository repo;

    @InjectMocks
    private EventServiceImpl service;

    private Event sample;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sample = new Event("T", "D", LocalDate.now(), "L", 50, "alice");
        sample.setId("1");
    }

    @Test
    void create_shouldReturnSavedEvent() {
        when(repo.save(sample)).thenReturn(sample);

        Event result = service.create(sample);
        assertSame(sample, result);
        verify(repo).save(sample);
    }

    @Test
    void findAll_shouldReturnList() {
        List<Event> list = List.of(sample);
        when(repo.findAll()).thenReturn(list);

        List<Event> result = service.findAll();
        assertEquals(1, result.size());
        assertEquals("T", result.get(0).getTitle());
    }

    @Test
    void findById_whenExists() {
        when(repo.findById("1")).thenReturn(Optional.of(sample));

        Optional<Event> result = service.findById("1");
        assertTrue(result.isPresent());
        assertEquals("alice", result.get().getCreatedBy());
    }

    @Test
    void findById_whenNotExists() {
        when(repo.findById("2")).thenReturn(Optional.empty());

        assertTrue(service.findById("2").isEmpty());
    }

    @Test
    void update_shouldSaveWithId() {
        Event updated = new Event("New","Desc", LocalDate.now(),"Loc",10,"alice");
        when(repo.save(any())).thenReturn(updated);

        Event result = service.update("1", updated);
        assertEquals("1", result.getId());
        verify(repo).save(updated);
    }

    @Test
    void delete_shouldInvokeRepository() {
        doNothing().when(repo).deleteById("1");
        service.delete("1");
        verify(repo).deleteById("1");
    }
}