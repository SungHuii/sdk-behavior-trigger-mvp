package com.behavior.sdk.trigger.common.security;

import com.behavior.sdk.trigger.common.exception.ErrorSpec;
import com.behavior.sdk.trigger.common.exception.FieldErrorDetail;
import com.behavior.sdk.trigger.common.exception.ServiceException;
import com.behavior.sdk.trigger.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    private static final long EXPIRATION_MS = 3600000; // 1h

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 공통 Claims Parser
    private Claims parseClaimsOrThrow(String token) {
        try {
            return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
        } catch (ExpiredJwtException e) {
            throw new ServiceException(
                ErrorSpec.AUTH_EXPIRED_TOKEN, // 1005, 401
                "만료된 토큰입니다.",
                List.of(new FieldErrorDetail("token", "expired", null))
            );
        } catch (JwtException | IllegalArgumentException e) {
            throw new ServiceException(
                ErrorSpec.AUTH_INVALID_TOKEN, // 1004, 401
                "유효하지 않은 토큰입니다.",
                List.of(new FieldErrorDetail("token", "invalid", token))
            );
        }
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaimsOrThrow(token);
            return true;
        } catch (ServiceException ignore) {
            return false;
        }
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = parseClaimsOrThrow(token);
        return UUID.fromString(claims.getSubject());
    }

    public void validateTokenOrThrow(String token) {
        parseClaimsOrThrow(token);
    }
}
