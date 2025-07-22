package com.keylee1110.eventapp_backend.service;

import com.keylee1110.eventapp_backend.model.Event;
import com.keylee1110.eventapp_backend.model.Registration;
import com.keylee1110.eventapp_backend.repository.EventRepository;
import com.keylee1110.eventapp_backend.repository.RegistrationRepository;
import com.keylee1110.eventapp_backend.service.RegistrationServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationServiceTest {

    @Mock
    private RegistrationRepository regRepo;
    @Mock
    private EventRepository eventRepo;
    @InjectMocks
    private RegistrationServiceImpl service;

    private Event event;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        event = new Event();
        event.setId("e1");
        event.setCapacity(2);
    }

    @Test
    void register_success() {
        when(regRepo.existsByUserIdAndEventId("u1", "e1")).thenReturn(false);
        when(eventRepo.findById("e1")).thenReturn(Optional.of(event));
        when(regRepo.countByEventId("e1")).thenReturn(1L);
        when(regRepo.save(any(Registration.class))).thenReturn(new Registration("u1", "e1", Instant.now()));

        Registration r = service.register("u1", "e1");
        assertEquals("u1", r.getUserId());
        assertEquals("e1", r.getEventId());
        assertNotNull(r.getTimestamp());
    }

    @Test
    void register_duplicate() {
        when(regRepo.existsByUserIdAndEventId("u1", "e1")).thenReturn(true);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.register("u1", "e1"));
        assertEquals("User already registered", ex.getMessage());
    }

    @Test
    void register_full() {
        when(regRepo.existsByUserIdAndEventId("u1", "e1")).thenReturn(false);
        when(eventRepo.findById("e1")).thenReturn(Optional.of(event));
        when(regRepo.countByEventId("e1")).thenReturn(2L);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.register("u1", "e1"));
        assertEquals("Event is full", ex.getMessage());
    }

    @Test
    void register_eventNotFound() {
        when(regRepo.existsByUserIdAndEventId("u1", "e1")).thenReturn(false);
        when(eventRepo.findById("e1")).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.register("u1", "e1"));
        assertEquals("Event not found", ex.getMessage());
    }
}