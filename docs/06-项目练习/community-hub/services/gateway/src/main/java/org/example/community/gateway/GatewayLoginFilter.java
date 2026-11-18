package org.example.community.gateway;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class GatewayLoginFilter implements GlobalFilter, Ordered {

    public static final String USER_HEADER = "X-User-Id";

    private final SecretKey key;

    public GatewayLoginFilter(@Value("${community.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod() == null ? "" : request.getMethod().name();
        ServerHttpRequest stripped = request.mutate().headers(headers -> headers.remove(USER_HEADER)).build();
        if (isPublic(method, path)) {
            return chain.filter(exchange.mutate().request(stripped).build());
        }
        try {
            Long userId = parseUserId(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
            ServerHttpRequest forwarded = stripped.mutate().header(USER_HEADER, userId.toString()).build();
            return chain.filter(exchange.mutate().request(forwarded).build());
        } catch (IllegalArgumentException | JwtException ex) {
            return unauthorized(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private boolean isPublic(String method, String path) {
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
        if ("/api/health".equals(path)
                || "/api/auth/register".equals(path)
                || "/api/auth/login".equals(path)) {
            return true;
        }
        if (!"GET".equalsIgnoreCase(method)) {
            return false;
        }
        return "/api/posts".equals(path)
                || "/api/posts/nearby".equals(path)
                || "/api/ranks/hot".equals(path)
                || "/rank/hot/list".equals(path)
                || path.matches("/api/posts/\\d+");
    }

    private Long parseUserId(String authorization) {
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

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        byte[] body = "{\"code\":401,\"message\":\"未登录\",\"data\":null}".getBytes(StandardCharsets.UTF_8);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
    }
}
