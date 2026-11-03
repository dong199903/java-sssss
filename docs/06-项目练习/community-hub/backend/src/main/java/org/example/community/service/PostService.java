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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.Optional;
@Service
@EnableScheduling
public class PostService {
  private static final int ADMIN_ROLE = 1;

  private final PostMapper postMapper;
  private final UserService userService;
  private final AdminOpLogMapper adminOpLogMapper;
  private final StringRedisTemplate stringRedisTemplate;
  private final ObjectMapper objectMapper;

  public PostService(PostMapper postMapper, UserService userService, AdminOpLogMapper adminOpLogMapper,
      StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
    this.postMapper = postMapper;
    this.userService = userService;
    this.adminOpLogMapper = adminOpLogMapper;
    this.stringRedisTemplate = stringRedisTemplate;
    this.objectMapper = objectMapper;
  }

  public List<String> getAllPosts() {
    return new ArrayList<String>();
  }

  public List<PostDto> getPostList(int communityId, Date lastCreateTime, Long lastId) {
    return toPostDtos(postMapper.selectByCommunityId(communityId, 10, lastCreateTime, lastId));
  }
  @Scheduled(fixedDelay = 5 * 60 * 1000)
  public void cleanViewCache() {
    //1. 每5分钟执行一次缓存任务,只清理集合前200个帖子
    for (int i = 0; i < 200; i++) {
      String postId = stringRedisTemplate.opsForSet().pop("post:view:dirty");
      if (postId == null) {
        return;
      }
      //2. lua执行清理缓存，删除缓存中对应的帖子浏览量，返回增加的浏览量
      Long delta = takeViewCount(postId);
      if (delta == null || delta <= 0) {
        continue;
      }
      try {
        if (postMapper.increaseViewCount(Long.valueOf(postId), delta) <= 0) {
          continue;
        }
      } catch (RuntimeException ex) {
        //3. 增加到mysql失败，恢复redis的集合和浏览量缓存
        stringRedisTemplate.opsForValue().increment("post:view:" + postId, delta);
        stringRedisTemplate.opsForSet().add("post:view:dirty", postId);
        continue;
      }
      //4. 如果redis缓存中还有数据，则恢复集合
      if (Boolean.TRUE.equals(stringRedisTemplate.hasKey("post:view:" + postId))) {
        stringRedisTemplate.opsForSet().add("post:view:dirty", postId);
      }
    }
  }

  private Long takeViewCount(String postId) {
    DefaultRedisScript<String> script = new DefaultRedisScript<>();
    script.setScriptText("local v = redis.call('GET', KEYS[1]) if not v then return nil end redis.call('DEL', KEYS[1]) return v");
    script.setResultType(String.class);
    String value = stringRedisTemplate.execute(script, List.of("post:view:" + postId));
    if (value == null || value.isEmpty()) {
      return null;
    }
    return Long.valueOf(value);
  }

  public PostDto getPost(Long id) {
    PostDto cached = readDetailCache(id);
    //增加浏览量
    stringRedisTemplate.opsForValue().increment("post:view:" + id);
    //标记当前有哪些帖子浏览量在缓存中
    stringRedisTemplate.opsForSet().add("post:view:dirty", String.valueOf(id));
    //1. 读取缓存，如果缓存中有数据，直接返回
    if (cached != null) {
      return cached;
    }
    //2. 如果缓存中没有数据，检查是否被标记为空，如果被标记为空，返回空
    if (nullMarked(id)) {
      return null;
    }
    //3. 如果缓存中没有数据，也没有被标记为空，则尝试获取redis锁
    String lockKey = "lock:post:" + id;
    String token = String.valueOf(IdWorker.getId());
    Boolean locked = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, token, 10, TimeUnit.SECONDS);
    if (Boolean.TRUE.equals(locked)) {
      //4. 如果获取到redis锁，先读取redis缓存
      //5. 如果redis缓存中有数据，直接返回
      try {
        cached = readDetailCache(id);
        if (cached != null) {
          return cached;
        }
        //6. 如果redis缓存中没有数据，检查是否被标记为空，如果被标记为空，返回空
        if (nullMarked(id)) {
          return null;
        }
        //7. 如果redis缓存中没有数据，也没有被标记为空，则尝试加载数据库数据
        return loadPost(id, true);
      } finally {
        //8. 释放redis锁
        unlock(lockKey, token);
      }
    }
    //9. 如果获取不到redis锁，重试3次
    for (int i = 0; i < 3; i++) {
      try {
        Thread.sleep(50);
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
        break;
      }
      //11. 读取redis缓存
      cached = readDetailCache(id);
      //12. 如果redis缓存中有数据，直接返回
      if (cached != null) {
        return cached;
      }
      //13. 如果redis缓存中没有数据，检查是否被标记为空，如果被标记为空，返回空
      if (nullMarked(id)) {
        return null;
      }
    }
    //10. 如果重试3次后还是没抢到redis锁，则尝试加载数据库数据
    return loadPost(id, false);
  }

  private PostDto readDetailCache(Long id) {
    String cacheValue = stringRedisTemplate.opsForValue().get("post:" + id);
    if (cacheValue == null) {
      return null;
    }
    try {
      return objectMapper.readValue(cacheValue, PostDto.class);
    } catch (JsonProcessingException ex) {
      return null;
    }
  }

  private boolean nullMarked(Long id) {
    return stringRedisTemplate.opsForValue().get("post:null:" + id) != null;
  }

  private PostDto loadPost(Long id, boolean writeCache) {
    Post post = postMapper.selectPostById(id);
    if (post == null) {
      if (writeCache) {
        stringRedisTemplate.opsForValue().set("post:null:" + id, "null", 60, TimeUnit.SECONDS);
      }
      return null;
    }
    PostDto postDto = toPostDto(post);
    if (writeCache) {
      try {
        long ttlSeconds = TimeUnit.MINUTES.toSeconds(30) + ThreadLocalRandom.current().nextInt(301);
        stringRedisTemplate.opsForValue().set("post:" + id, objectMapper.writeValueAsString(postDto), ttlSeconds,
            TimeUnit.SECONDS);
      } catch (JsonProcessingException ex) {
        System.err.println("缓存更新失败: " + ex.getMessage());
      }
    }
    return postDto;
  }

  private void unlock(String lockKey, String token) {
    DefaultRedisScript<Long> script = new DefaultRedisScript<>();
    script.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
    script.setResultType(Long.class);
    stringRedisTemplate.execute(script, List.of(lockKey), token);
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
    postDto.setNickname(post.getNickname());
    postDto.setViewCount(post.getViewCount());
    postDto.setLikeCount(post.getLikeCount());
    postDto.setCommentCount(post.getCommentCount());
    postDto.setFavoriteCount(post.getFavoriteCount());
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
    p.setId(IdWorker.getId());
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
