package org.example.community.web;

import org.example.community.auth.JwtService;
import org.example.community.common.ApiResponse;
import org.example.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.example.community.dto.UserDto;
import org.example.community.entity.User;
import java.time.LocalDateTime;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
  @Autowired
  private UserService userService;
  @Autowired
  private JwtService jwtService;

  @PostMapping("/create")
  public ApiResponse<String> createUser(@RequestBody UserDto userDto) {
    /**
     * 1. 参数校验：验证用户名、密码、昵称、城市代码、角色是否符合要求
     */
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
    // 2. 用户名唯一性校验
    User user = userService.getUserByUsername(userDto.getUsername());
    if (user != null) {
      return ApiResponse.error("用户名已存在");
    }
    // 3. 密码加密
    String passwordHash = DigestUtils.md5DigestAsHex(userDto.getPasswordHash().getBytes());
    if (passwordHash == null || passwordHash.isEmpty()) {
      return ApiResponse.error("密码加密失败");
    }
    // 4. 模型转换：UserDto -> UserEntity
    User userEntity = new User();
    userEntity.setUsername(userDto.getUsername());
    userEntity.setPasswordHash(passwordHash);
    userEntity.setNickname(userDto.getNickname());
    userEntity.setCityCode(userDto.getCityCode());
    userEntity.setRole(userDto.getRole());
    userEntity.setCreatedAt(LocalDateTime.now());
    return ApiResponse.ok(userService.createUser(userEntity).getId().toString());
  }

  @PostMapping("/login")
  public ApiResponse<String> loginUser(@RequestBody UserDto userDto) {
    /**
     * 1. 参数校验：验证用户名、密码是否符合要求
     */
    if (userDto == null) {
      return ApiResponse.error("用户信息不能为空");
    }
    if (userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
      return ApiResponse.error("用户名不能为空");
    }
    if (userDto.getPasswordHash() == null || userDto.getPasswordHash().isEmpty()) {
      return ApiResponse.error("密码不能为空");
    }
    // 2. 用户名唯一性校验
    User user = userService.getUserByUsername(userDto.getUsername());
    if (user == null) {
      return ApiResponse.error("用户不存在");
    }
    // 3. 密码判断
    String passwordHash = DigestUtils.md5DigestAsHex(userDto.getPasswordHash().getBytes());
    if (!user.getPasswordHash().equals(passwordHash)) {
      return ApiResponse.error("密码错误");
    }
    return ApiResponse.ok(jwtService.issue(user),user.getUsername()+"登录成功");
  }
}
