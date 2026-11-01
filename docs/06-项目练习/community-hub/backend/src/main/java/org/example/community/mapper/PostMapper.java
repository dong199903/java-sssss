package org.example.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.community.entity.Post;
import java.util.List;
import java.util.Date;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
  int insert(Post post);
  List<Post> selectByCommunityId(int communityId, int pageSize, Date lastCreateTime, Long lastId);

  List<Post> selectNearby(String geohashStart, String geohashEnd, int pageSize, Date lastCreateTime, Long lastId);

  Post selectPostById(Long id);

  int updatePost(@Param("id") Long id, @Param("authorId") Long authorId,
          @Param("title") String title, @Param("content") String content);

  int deletePost(Long id);
}
