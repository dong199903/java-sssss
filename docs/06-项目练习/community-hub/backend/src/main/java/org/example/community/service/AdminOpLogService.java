package org.example.community.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.community.dto.UserDto;
import org.example.community.entity.AdminOpLog;
import org.example.community.mapper.AdminOpLogMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminOpLogService {

    private static final int ADMIN_ROLE = 1;

    private final AdminOpLogMapper adminOpLogMapper;
    private final UserService userService;

    public AdminOpLogService(AdminOpLogMapper adminOpLogMapper, UserService userService) {
        this.adminOpLogMapper = adminOpLogMapper;
        this.userService = userService;
    }

    public boolean isAdmin(Long userId) {
        UserDto user = userService.getUserById(userId);
        return user != null && Integer.valueOf(ADMIN_ROLE).equals(user.getRole());
    }

    public List<AdminOpLog> listLatest() {
        return adminOpLogMapper.selectList(new QueryWrapper<AdminOpLog>()
                .orderByDesc("created_at")
                .last("LIMIT 50"));
    }
}
