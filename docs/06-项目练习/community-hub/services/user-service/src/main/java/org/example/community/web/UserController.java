package org.example.community.web;

import org.example.community.auth.JwtService;
import org.example.community.common.ApiResponse;
import org.example.community.dto.UserDto;
import org.example.community.entity.User;
import org.example.community.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody UserDto userDto) {
        if (userDto == null) {
            return ApiResponse.error("用户信息不能为空");
        }
        if (userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            return ApiResponse.error("用户名不能为空");
        }
        if (userDto.getPasswordHash() == null || userDto.getPasswordHash().isEmpty()) {
            return ApiResponse.error("密码不能为空");
        }
        if (userDto.getNickname() == null || userDto.getNickname().isEmpty()) {
            return ApiResponse.error("昵称不能为空");
        }
        if (userService.getUserByUsername(userDto.getUsername()) != null) {
            return ApiResponse.error("用户名已存在");
        }
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPasswordHash(DigestUtils.md5DigestAsHex(userDto.getPasswordHash().getBytes()));
        user.setNickname(userDto.getNickname());
        user.setCityCode(userDto.getCityCode());
        user.setRole(userDto.getRole() == null ? 0 : userDto.getRole());
        user.setCreatedAt(LocalDateTime.now());
        return ApiResponse.ok(userService.createUser(user).getId().toString());
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody UserDto userDto) {
        if (userDto == null) {
            return ApiResponse.error("用户信息不能为空");
        }
        if (userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            return ApiResponse.error("用户名不能为空");
        }
        if (userDto.getPasswordHash() == null || userDto.getPasswordHash().isEmpty()) {
            return ApiResponse.error("密码不能为空");
        }
        User user = userService.getUserByUsername(userDto.getUsername());
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        String passwordHash = DigestUtils.md5DigestAsHex(userDto.getPasswordHash().getBytes());
        if (!user.getPasswordHash().equals(passwordHash)) {
            return ApiResponse.error("密码错误");
        }
        return ApiResponse.ok(jwtService.issue(user), user.getUsername() + "登录成功");
    }
}
