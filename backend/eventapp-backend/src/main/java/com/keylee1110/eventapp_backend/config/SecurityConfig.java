package com.keylee1110.eventapp_backend.config;

import com.keylee1110.eventapp_backend.service.UserDetailsServiceImpl;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enable method-level security
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl uds) {
        this.userDetailsService = uds;
    }

    @Bean
    public SecretKey jwtSigningKey() {
        byte[] keyBytes = new byte[32];                     // 32 bytes = 256 bits
        new SecureRandom().nextBytes(keyBytes);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(SecretKey jwtSigningKey) {
        // JwtAuthenticationFilter đã được sửa để constructor nhận (SecretKey, UserDetailsService)
        return new JwtAuthenticationFilter(jwtSigningKey, userDetailsService);
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           SecretKey jwtSigningKey) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/api/v0/events").permitAll()
                        .requestMatchers("/api/v0/**").authenticated()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter(jwtSigningKey),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}