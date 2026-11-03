package org.example.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.community.dto.CommentDto;
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

  int likePost(Long likeId, Long postId, Long userId);
  int updateLikeCount(Long id, int count);
  int deleteLike(Long postId, Long userId);
  int decreaseLikeCount(Long id);

  int favoritePost(Long favoriteId, Long postId, Long userId);
  int updateFavoriteCount(Long id, int count);
  int deleteFavorite(Long postId, Long userId);
  int decreaseFavoriteCount(Long id);

  int insertComment(@Param("id") Long id, @Param("postId") Long postId,
          @Param("authorId") Long authorId, @Param("content") String content);

  int increaseCommentCount(Long id);

  int increaseViewCount(@Param("id") Long id, @Param("delta") long delta);

  List<CommentDto> selectComments(@Param("postId") Long postId, @Param("pageSize") int pageSize,
          @Param("lastCreateTime") Date lastCreateTime, @Param("lastId") Long lastId);
}
