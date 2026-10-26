package org.example.community;

import org.example.community.entity.User;
import org.example.community.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void createUserInsertsRow() {
        User user = new User();
        user.setUsername("user_" + System.nanoTime());
        user.setPasswordHash("hash");
        user.setNickname("昵称");
        user.setCityCode("310000");
        user.setRole(0);

        User saved = userService.createUser(user);

        assertNotNull(saved.getId());
        assertEquals(saved.getUsername(), userService.getUserByUsername(saved.getUsername()).getUsername());
    }
}
