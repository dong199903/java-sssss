package org.example.community.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.community.service.PostService;
import org.example.community.common.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.example.community.dto.PostDto;
import org.example.community.auth.JwtService;
import org.example.community.service.UserService;
import org.example.community.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
@RestController
@RequestMapping("/api/posts")
public class PostController {
  @Autowired
  private PostService postService;
  @PostMapping("/post")
  public ApiResponse<String> postArticle(HttpServletRequest request, @RequestBody PostDto postDto) {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      return ApiResponse.error("未登录");
    }
    /**
     * 发帖接口
     * 1. 获取用户信息
     * 2. 获取帖子内容
     * 3. 保存帖子
     * 4. 返回结果
     */
    int result = postService.postArticle(userId, postDto);
    if (result > 0) {
      return ApiResponse.ok("success");
    } else {
      return ApiResponse.error("发帖失败");
    }
  }
}
