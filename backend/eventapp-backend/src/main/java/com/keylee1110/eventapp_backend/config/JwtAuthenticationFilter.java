package com.keylee1110.eventapp_backend.config;

import io.jsonwebtoken.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JwtAuthenticationFilter extends GenericFilterBean {
    private final String secret;
    private final UserDetailsService uds;

    public JwtAuthenticationFilter(String secret, UserDetailsService uds) {
        this.secret = secret;
        this.uds = uds;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String token = parseToken(request);
        if (token != null) {
            try {
                Jws<Claims> claims = Jwts.parser()
                        .setSigningKey(secret.getBytes())
                        .build()
                        .parseClaimsJws(token);

                String username = claims.getBody().getSubject();
                var userDetails = uds.loadUserByUsername(username);
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JwtException e) {
                // invalid token — bạn có thể log hoặc trả 401
            }
        }
        chain.doFilter(req, res);
    }

    private String parseToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
