package org.example.community.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.community.common.ApiResponse;
import org.example.community.service.UserService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class LoginFilter extends OncePerRequestFilter {

    public static final String USER_ID = "userId";

    private final JwtService jwtService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public LoginFilter(JwtService jwtService, UserService userService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        if ("/api/health".equals(path)
                || "/api/auth/register".equals(path)
                || "/api/auth/login".equals(path)) {
            return true;
        }
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        return "/api/posts".equals(path)
                || "/api/posts/nearby".equals(path)
                || "/api/ranks/hot".equals(path)
                || path.matches("/api/posts/\\d+");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Long userId = jwtService.parseUserId(request.getHeader("Authorization"));
            if (userService.getUserById(userId) == null) {
                writeUnauthorized(response, "用户不存在");
                return;
            }
            request.setAttribute(USER_ID, userId);
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException | JwtException ex) {
            writeUnauthorized(response, "未登录");
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(new ApiResponse<>(401, message, null)));
    }
}
