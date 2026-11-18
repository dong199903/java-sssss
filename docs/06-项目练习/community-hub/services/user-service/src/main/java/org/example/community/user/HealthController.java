package org.example.community.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/internal/health")
    public Map<String, String> health() {
        return Map.of("service", "user-service");
    }
}
