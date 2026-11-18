package org.example.community.content;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/posts")
public class PostExistController {

    @GetMapping("/{id}/exists")
    public boolean exists(@PathVariable("id") Long id) {
        throw new UnsupportedOperationException("在 community_content.post 里查这篇帖子是否存在");
    }
}
