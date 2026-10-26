package org.example.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.community.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
  int insert(User user);

  User selectByUsername(@Param("username") String username);
}
