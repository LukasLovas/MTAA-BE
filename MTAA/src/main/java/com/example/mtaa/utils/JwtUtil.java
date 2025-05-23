package com.example.mtaa.utils;

import com.example.mtaa.model.User;
import com.example.mtaa.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long expirationTime = 24 * 60 * 60 * 1000;

    public static String generateToken(String username, Long id) {
        return Jwts.builder()
                .setSubject(username)
                .claim("user_id", id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public static String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static Long extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("user_id", Long.class);
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Component
    public class JwtUser {

        private static UserRepository userRepository;

        @Autowired
        public JwtUser(UserRepository userRepository) {
            JwtUser.userRepository = userRepository;
        }

        public static String getUsernameByUserId(Long userId) {
            User user = userRepository.findById(userId).orElse(null);
            return user != null ? user.getUsername() : null;
        }
    }
}
