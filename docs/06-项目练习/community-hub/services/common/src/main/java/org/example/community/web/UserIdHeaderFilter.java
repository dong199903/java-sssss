package org.example.community.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserIdHeaderFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-User-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String raw = request.getHeader(HEADER);
        if (raw != null && !raw.isBlank()) {
            try {
                request.setAttribute("userId", Long.valueOf(raw));
            } catch (NumberFormatException ex) {
                request.removeAttribute("userId");
            }
        }
        filterChain.doFilter(request, response);
    }
}
