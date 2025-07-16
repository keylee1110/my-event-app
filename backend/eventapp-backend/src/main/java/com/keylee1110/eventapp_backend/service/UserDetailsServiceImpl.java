//package com.keylee1110.eventapp_backend.service;
//
//import com.keylee1110.eventapp_backend.model.User;
//import com.keylee1110.eventapp_backend.repository.UserRepository;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.stereotype.Service;
//import java.util.stream.Collectors;
//
//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {
//    private final UserRepository repo;
//    public UserDetailsServiceImpl(UserRepository repo) { this.repo = repo; }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = repo.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(),
//                user.getRoles().stream()
//                        .map(SimpleGrantedAuthority::new)
//                        .collect(Collectors.toList())
//        );
//    }
//}