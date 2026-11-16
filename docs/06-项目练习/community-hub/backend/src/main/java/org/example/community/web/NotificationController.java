package org.example.community.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.community.common.ApiResponse;
import org.example.community.entity.Notification;
import org.example.community.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<List<Notification>> list(HttpServletRequest request, HttpServletResponse response) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return new ApiResponse<>(401, "未登录", null);
        }
        return ApiResponse.ok(notificationService.listUnread(userId));
    }

    @PostMapping("/{id}/read")
    public ApiResponse<String> read(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return ApiResponse.error("未登录");
        }
        int result = notificationService.markRead(userId, id);
        if (result == 2) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return ApiResponse.error("通知不存在");
        }
        if (result == 3) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ApiResponse.error("无权操作");
        }
        return ApiResponse.ok("已读");
    }
}
