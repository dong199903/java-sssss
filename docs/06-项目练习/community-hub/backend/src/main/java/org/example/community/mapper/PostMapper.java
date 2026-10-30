package org.example.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.community.entity.Post;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
  int insert(Post post);
}
