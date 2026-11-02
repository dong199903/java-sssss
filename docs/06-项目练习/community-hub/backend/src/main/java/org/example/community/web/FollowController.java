package org.example.community.web;

import org.example.community.common.ApiResponse;
import org.example.community.service.UserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class FollowController {

  private final UserService userService;

  public FollowController(UserService userService) {
    this.userService = userService;
  }

  /**关注用户 */
  @PostMapping("/{id}/follow")
  public ApiResponse<String> follow(@PathVariable Long id, HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      return ApiResponse.error("未登录");
    }
    int result = userService.follow(userId, id);
    if (result == 1) {
      return ApiResponse.ok("success");
    }
    if (result == -1) {
      return ApiResponse.error("已关注");
    }
    if (result == 2) {
      return ApiResponse.error("不能关注自己");
    }
    if (result == 3) {
      return ApiResponse.error("用户不存在");
    }
    return ApiResponse.error("关注失败");
  }
}