package org.example.community.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.community.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expireMillis;

    public JwtService(
            @Value("${community.jwt.secret}") String secret,
            @Value("${community.jwt.expire-days}") long expireDays) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMillis = expireDays * 24 * 60 * 60 * 1000;
    }

    public String issue(User user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expireMillis))
                .signWith(key)
                .compact();
    }

    public Long parseUserId(String authorization) {
      if (authorization == null || !authorization.startsWith("Bearer ")) {
          throw new IllegalArgumentException("未登录");
      }
      String token = authorization.substring("Bearer ".length());
      String subject = Jwts.parser()
              .verifyWith(key)
              .build()
              .parseSignedClaims(token)
              .getPayload()
              .getSubject();
      return Long.valueOf(subject);
  }
}
