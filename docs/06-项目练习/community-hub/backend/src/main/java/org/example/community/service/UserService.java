package org.example.community.service;

import org.example.community.entity.User;
import org.example.community.mapper.UserMapper;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User createUser(User user) {
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new RuntimeException("创建用户失败");
        }
        return user;
    }
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
}
