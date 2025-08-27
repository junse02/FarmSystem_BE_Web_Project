package com.example.board.auth.service;

import com.example.board.auth.JwtProvider;
import com.example.board.auth.TokenStore;
import com.example.board.user.domain.User;
import com.example.board.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtProvider jwt;
    private final TokenStore tokenStore;

    @Value("${jwt.issuer}") private String issuer;
    @Value("${jwt.refresh-exp-seconds}") private long refreshExp;
    @Value("${jwt.secret}") private String secret; // refresh도 같은 키 사용(원하면 분리)

    @Transactional
    public void signup(String email, String rawPw, String name) {
        if (users.existsByEmailAndIsDeletedFalse(email))
            throw new IllegalStateException("DUPLICATE_EMAIL");
        var u = new User();
        u.setEmail(email);
        u.setPassword(encoder.encode(rawPw));
        u.setName(name);
        users.save(u);
    }

    @Transactional
    public Map<String, Object> login(String email, String rawPw) {
        var u = users.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS"));

        System.out.println("ENCODER CLASS: " + encoder.getClass());
        System.out.println("RAW: " + rawPw);
        System.out.println("HASHED: " + u.getPassword());
        System.out.println("MATCH: " + encoder.matches(rawPw, u.getPassword()));

        if (!encoder.matches(rawPw, u.getPassword()))
            throw new IllegalArgumentException("INVALID_CREDENTIALS");

        String accessJti = UUID.randomUUID().toString();
        String access = jwt.generateAccess(u.getId(), accessJti);

        String refreshJti = UUID.randomUUID().toString();
        Instant now = Instant.now();
        String refresh = Jwts.builder()
                .setIssuer(issuer)
                .setSubject(u.getId().toString())
                .setId(refreshJti)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(refreshExp)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        tokenStore.saveRefresh(u.getId(), refreshJti, refresh);

        return Map.of("accessToken", access, "refreshToken", refresh, "expiresIn", 900);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> refresh(String refreshToken) {
        Claims c = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                .build().parseClaimsJws(refreshToken).getBody();

        Long userId = Long.valueOf(c.getSubject());
        String jti = c.getId();
        if (!tokenStore.existsRefresh(userId, jti))
            throw new IllegalArgumentException("REFRESH_NOT_FOUND");

        String newAccess = jwt.generateAccess(userId, UUID.randomUUID().toString());
        return Map.of("accessToken", newAccess, "expiresIn", 900);
    }

    @Transactional
    public void logout(Long userId, String refreshToken) {
        Claims c = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                .build().parseClaimsJws(refreshToken).getBody();
        if (!Objects.equals(Long.valueOf(c.getSubject()), userId)) return;
        tokenStore.deleteRefresh(userId, c.getId());
    }
}
