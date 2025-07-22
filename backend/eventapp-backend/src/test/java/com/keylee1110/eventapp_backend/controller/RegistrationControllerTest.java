package com.keylee1110.eventapp_backend.controller;

import com.keylee1110.eventapp_backend.config.SecurityConfig;
import com.keylee1110.eventapp_backend.model.Registration;
import com.keylee1110.eventapp_backend.service.RegistrationService;
import com.keylee1110.eventapp_backend.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "u1")
class RegistrationControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RegistrationService regService;

    @MockBean
    UserDetailsServiceImpl userDetailsService;


    @Test
    void postRegister_success() throws Exception {
        Registration reg = new Registration("u1", "e1", Instant.now());
        when(regService.register("u1", "e1")).thenReturn(reg);

        mvc.perform(post("/api/v0/events/e1/register"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("u1"))
                .andExpect(jsonPath("$.eventId").value("e1"));
    }

    @Test
    void postRegister_duplicate() throws Exception {
        when(regService.register("u1", "e1"))
                .thenThrow(new IllegalStateException("User already registered"));

        mvc.perform(post("/api/v0/events/e1/register"))
                .andExpect(status().isConflict())
                .andExpect(content().string("User already registered"));
    }

    @Test
    void postRegister_eventNotFound() throws Exception {
        when(regService.register("u1", "e1"))
                .thenThrow(new IllegalArgumentException("Event not found"));

        mvc.perform(post("/api/v0/events/e1/register"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Event not found"));
    }

    @Test
    @WithMockUser(username = "u2")
    void getUserRegistrations_forbidden() throws Exception {
        mvc.perform(get("/api/v0/users/u1/registrations"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserRegistrations_success() throws Exception {
        Registration r = new Registration("u1", "e1", Instant.now());
        when(regService.getByUser("u1")).thenReturn(List.of(r));

        mvc.perform(get("/api/v0/users/u1/registrations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value("e1"));
    }
}

