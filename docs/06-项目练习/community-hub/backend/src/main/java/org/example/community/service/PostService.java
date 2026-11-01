package org.example.community.service;

import java.util.List;
import java.util.ArrayList;
import org.example.community.common.GeoHash;
import org.example.community.dto.CommentDto;
import org.example.community.dto.PostDto;
import org.example.community.dto.UserDto;
import org.example.community.entity.AdminOpLog;
import org.example.community.mapper.AdminOpLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.community.mapper.PostMapper;
import org.example.community.entity.Post;
import java.util.Date;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.dao.DuplicateKeyException;

@Service
public class PostService {
  private static final int ADMIN_ROLE = 1;

  private final PostMapper postMapper;
  private final UserService userService;
  private final AdminOpLogMapper adminOpLogMapper;

  public PostService(PostMapper postMapper, UserService userService, AdminOpLogMapper adminOpLogMapper) {
    this.postMapper = postMapper;
    this.userService = userService;
    this.adminOpLogMapper = adminOpLogMapper;
  }
  public List<String> getAllPosts() {
    return new ArrayList<String>();
  }
  public List<PostDto> getPostList(int communityId, Date lastCreateTime, Long lastId) {
    return toPostDtos(postMapper.selectByCommunityId(communityId, 10, lastCreateTime, lastId));
  }

  public PostDto getPost(Long id) {
    Post post = postMapper.selectPostById(id);
    if (post == null) {
      return null;
    }
    return toPostDto(post);
  }

  public List<PostDto> getNearbyPosts(double latitude, double longitude, Date lastCreateTime, Long lastId) {
    String geohashStart = GeoHash.encode(latitude, longitude, 5);
    String geohashEnd = GeoHash.nextPrefix(geohashStart);
    return toPostDtos(postMapper.selectNearby(geohashStart, geohashEnd, 10, lastCreateTime, lastId));
  }

  private List<PostDto> toPostDtos(List<Post> posts) {
    List<PostDto> postDtos = new ArrayList<PostDto>();
    for (Post post : posts) {
      postDtos.add(toPostDto(post));
    }
    return postDtos;
  }

  private PostDto toPostDto(Post post) {
    PostDto postDto = new PostDto();
    postDto.setId(post.getId());
    postDto.setCommunityId(post.getCommunityId());
    postDto.setUserId(post.getAuthorId());
    postDto.setTitle(post.getTitle());
    postDto.setContent(post.getContent());
    postDto.setCreateTime(post.getCreateTime());
    postDto.setUpdateTime(post.getUpdateTime());
    postDto.setLatitude(post.getLatitude());
    postDto.setLongitude(post.getLongitude());
    return postDto;
  }
  public int postArticle(Long userId, PostDto postDto) {
    if (postDto.getLatitude() == null || postDto.getLongitude() == null) {
      return 0;
    }
    Post p = new Post();
    p.setCommunityId(postDto.getCommunityId());
    p.setAuthorId(userId);
    p.setTitle(postDto.getTitle());
    p.setContent(postDto.getContent());
    p.setLatitude(postDto.getLatitude());
    p.setLongitude(postDto.getLongitude());
    p.setGeohash(GeoHash.encode(postDto.getLatitude().doubleValue(), postDto.getLongitude().doubleValue(), 6));
    p.setCreateTime(new Date());
    p.setUpdateTime(new Date());
    int result = postMapper.insert(p);
    if (result > 0) {
      return 1;
    } else {
      return 0;
    }
  }

  @Transactional
  public int updatePost(Long userId, Long id, PostDto postDto) {
    if (postDto.getTitle() == null || postDto.getTitle().isEmpty()
            || postDto.getContent() == null || postDto.getContent().isEmpty()) {
      return 3;
    }
    Post post = postMapper.selectPostById(id);
    if (post == null) {
      return 0;
    }
    if (!userId.equals(post.getAuthorId())) {
      return 2;
    }
    return postMapper.updatePost(id, userId, postDto.getTitle(), postDto.getContent()) > 0 ? 1 : 0;
  }

  @Transactional
  public int deletePost(Long userId, Long id) {
    Post post = postMapper.selectPostById(id);
    if (post == null) {
      return 0;
    }
    boolean author = userId.equals(post.getAuthorId());
    boolean admin = isAdmin(userId);
    if (!author && !admin) {
      return 2;
    }
    if (postMapper.deletePost(id) <= 0) {
      return 0;
    }
    if (admin) {
      AdminOpLog log = new AdminOpLog();
      log.setOperatorId(userId);
      log.setAction("DELETE_POST");
      log.setTargetType("post");
      log.setTargetId(id);
      log.setDetail(deleteDetail(post.getTitle()));
      adminOpLogMapper.insert(log);
    }
    return 1;
  }

  private boolean isAdmin(Long userId) {
    UserDto user = userService.getUserById(userId);
    return user != null && Integer.valueOf(ADMIN_ROLE).equals(user.getRole());
  }

  private String deleteDetail(String title) {
    String detail = "删除帖子：" + title;
    return detail.length() <= 500 ? detail : detail.substring(0, 500);
  }

  @Transactional
  public int likePost(Long userId, Long id) {
    Post post = postMapper.selectPostById(id);
    if (post == null) {
      return 2;
    }
    try {
      postMapper.likePost(IdWorker.getId(), id, userId);
    } catch (DuplicateKeyException ex) {
      return -1;
    }
    return postMapper.updateLikeCount(id, 1) > 0 ? 1 : 0;
  }

  @Transactional
  public int unlikePost(Long userId, Long id) {
    Post post = postMapper.selectPostById(id);
    if (post == null) {
      return 2;
    }
    if (postMapper.deleteLike(id, userId) <= 0) {
      return -1;
    }
    if (postMapper.decreaseLikeCount(id) <= 0) {
      throw new IllegalStateException("点赞计数更新失败");
    }
    return 1;
  }

  @Transactional
  public int favoritePost(Long userId, Long id) {
    Post post = postMapper.selectPostById(id);
    if (post == null) {
      return 2;
    }
    try {
      postMapper.favoritePost(IdWorker.getId(), id, userId);
    } catch (DuplicateKeyException ex) {
      return -1;
    }
    return postMapper.updateFavoriteCount(id, 1) > 0 ? 1 : 0;
  }

  @Transactional
  public int unfavoritePost(Long userId, Long id) {
    Post post = postMapper.selectPostById(id);
    if (post == null) {
      return 2;
    }
    if (postMapper.deleteFavorite(id, userId) <= 0) {
      return -1;
    }
    if (postMapper.decreaseFavoriteCount(id) <= 0) {
      throw new IllegalStateException("收藏计数更新失败");
    }
    return 1;
  }

  @Transactional
  public int addComment(Long userId, Long id, String content) {
    if (content == null || content.isBlank()) {
      return 3;
    }
    if (content.length() > 1000) {
      return 4;
    }
    Post post = postMapper.selectPostById(id);
    if (post == null) {
      return 2;
    }
    postMapper.insertComment(IdWorker.getId(), id, userId, content.trim());
    if (postMapper.increaseCommentCount(id) <= 0) {
      throw new IllegalStateException("评论计数更新失败");
    }
    return 1;
  }

  public List<CommentDto> getComments(Long id, Date lastCreateTime, Long lastId) {
    if (postMapper.selectPostById(id) == null) {
      return null;
    }
    return postMapper.selectComments(id, 20, lastCreateTime, lastId);
  }
}
