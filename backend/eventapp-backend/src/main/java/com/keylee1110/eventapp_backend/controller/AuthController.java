package com.keylee1110.eventapp_backend.controller;

import com.keylee1110.eventapp_backend.model.User;
import com.keylee1110.eventapp_backend.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired private AuthenticationManager authManager;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

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
                new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword()));
        // nếu auth thành công:
        String token = Jwts.builder()
                .setSubject(u.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1h
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .compact();

        return ResponseEntity.ok(Map.of("token", token));
    }
}