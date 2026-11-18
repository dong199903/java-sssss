package org.example.community.interaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "content-service", fallback = ContentPostClientFallback.class)
public interface ContentPostClient {

    @GetMapping("/internal/posts/{id}/exists")
    boolean exists(@PathVariable("id") Long id);
}
