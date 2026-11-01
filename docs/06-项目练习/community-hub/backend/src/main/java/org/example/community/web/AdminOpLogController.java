package org.example.community.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.community.common.ApiResponse;
import org.example.community.entity.AdminOpLog;
import org.example.community.service.AdminOpLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminOpLogController {

    private final AdminOpLogService adminOpLogService;

    public AdminOpLogController(AdminOpLogService adminOpLogService) {
        this.adminOpLogService = adminOpLogService;
    }

    /**管理操作日志，仅管理员 */
    @GetMapping("/op-logs")
    public ApiResponse<List<AdminOpLog>> list(HttpServletRequest request, HttpServletResponse response) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(null, "未登录");
        }
        if (!adminOpLogService.isAdmin(userId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return new ApiResponse<>(403, "无权查看", null);
        }
        return ApiResponse.ok(adminOpLogService.listLatest());
    }
}
