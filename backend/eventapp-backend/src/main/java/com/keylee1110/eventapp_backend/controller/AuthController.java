package com.keylee1110.eventapp_backend.controller;

import com.keylee1110.eventapp_backend.model.User;
import com.keylee1110.eventapp_backend.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    private final SecretKey jwtSigningKey;

    public AuthController(AuthenticationManager authManager,
                          UserRepository userRepo,
                          PasswordEncoder passwordEncoder,
                          SecretKey jwtSigningKey) {
        this.authManager = authManager;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtSigningKey = jwtSigningKey;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User u) {
        if (userRepo.findByUsername(u.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username exists");
        }
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        u.setRoles(List.of("ROLE_USER"));
        userRepo.save(u);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String,String>> signin(@RequestBody User u) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword())
        );

        String token = Jwts.builder()
                .setSubject(u.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000)) // 1h
                .signWith(jwtSigningKey)  // dùng SecretKey
                .compact();

        return ResponseEntity.ok(Map.of("token", token));
    }
}