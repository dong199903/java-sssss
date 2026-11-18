package org.example.community.interaction.client;

import org.springframework.stereotype.Component;

@Component
public class ContentPostClientFallback implements ContentPostClient {

    @Override
    public boolean exists(Long id) {
        throw new IllegalStateException("服务繁忙");
    }
}
