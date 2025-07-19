package com.keylee1110.eventapp_backend.controller;

import com.keylee1110.eventapp_backend.config.SecurityConfig;
import com.keylee1110.eventapp_backend.model.Event;
import com.keylee1110.eventapp_backend.service.EventService;
import com.keylee1110.eventapp_backend.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.*;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@Import({SecurityConfig.class})
class EventControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EventService service;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @WithMockUser(username="alice")
    @Test
    void createEvent_shouldReturn201AndBody() throws Exception {
        Event sample = new Event("T","D", LocalDate.now(),"L", 5,"alice");
        sample.setId("1");
        Mockito.when(service.create(any())).thenReturn(sample);

        String json = """
            {
              "title":"T",
              "description":"D",
              "date":"%s",
              "location":"L",
              "capacity":5
            }
            """.formatted(LocalDate.now());

        mvc.perform(post("/api/v0/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.createdBy").value("alice"));
    }

    @Test
    void getAllEvents_shouldReturnList() throws Exception {
        Event e = new Event("X","Y", LocalDate.now(),"Z",10,"bob");
        e.setId("2");
        Mockito.when(service.findAll()).thenReturn(List.of(e));

        mvc.perform(get("/api/v0/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("X"));
    }

    @WithMockUser
    @Test
    void getOne_whenNotFound_shouldReturn404() throws Exception {
        Mockito.when(service.findById("99")).thenReturn(Optional.empty());

        mvc.perform(get("/api/v0/events/99"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username="alice")
    @Test
    void updateEvent_whenForbidden_shouldReturn403() throws Exception {
        Event existing = new Event("A","B", LocalDate.now(),"C",1,"bob");
        existing.setId("5");

        Mockito.when(service.findById("5")).thenReturn(Optional.of(existing));

        String json = """
            {"title":"A","description":"B","date":"%s","location":"C","capacity":1}
            """.formatted(LocalDate.now());

        mvc.perform(put("/api/v0/events/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username="bob")
    @Test
    void deleteEvent_whenOwner_shouldReturn204() throws Exception {
        Event existing = new Event("A","B", LocalDate.now(),"C",1,"bob");
        existing.setId("5");
        Mockito.when(service.findById("5")).thenReturn(Optional.of(existing));

        mvc.perform(delete("/api/v0/events/5"))
                .andExpect(status().isNoContent());
    }
}