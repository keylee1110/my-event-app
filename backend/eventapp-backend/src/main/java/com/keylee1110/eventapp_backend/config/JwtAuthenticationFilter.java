package com.keylee1110.eventapp_backend.config;

import io.jsonwebtoken.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Claims;


import javax.crypto.SecretKey;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecretKey signingKey;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(SecretKey signingKey,
                                   UserDetailsService userDetailsService) {
        this.signingKey = signingKey;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7).trim();
        try {
            // parse & verify token
            Jws<Claims> jws = Jwts.parser()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);

            String username = jws.getBody().getSubject();
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}
