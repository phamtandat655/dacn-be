package com.example.dacn.util;

import com.example.dacn.model.Token;
import com.example.dacn.model.User;
import com.example.dacn.repository.TokenRepository;
import com.example.dacn.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private Key SECRET_KEY;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secretKeyString;

    @PostConstruct
    public void init() {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();

        // Lưu token vào cơ sở dữ liệu
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user != null) {
            List<Token> existingTokens = tokenRepository.findByUserOrderByCreatedAtAsc(user);
            if (existingTokens.size() >= 2) {
                // Xóa token cũ nhất nếu đã có 2 token
                tokenRepository.delete(existingTokens.get(0));
            }

            Token newToken = new Token();
            newToken.setUser(user);
            newToken.setToken(token);
            tokenRepository.save(newToken);
        }

        return token;
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return username.equals(userDetails.getUsername()) && !expiration.before(new Date());
    }
}

