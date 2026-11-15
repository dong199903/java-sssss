package org.example.community.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.example.community.dto.PostDto;
import org.example.community.entity.Post;
import org.example.community.mapper.PostMapper;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class RankHotService {
  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Autowired
  private PostService postService;

  @Autowired
  private PostMapper postMapper;

  /**30分钟自动处理热榜数据 */
  @Scheduled(fixedDelay = 30 * 60 * 1000)
  public void processHotPosts() {
    try {
      Set<ZSetOperations.TypedTuple<String>> ranked = stringRedisTemplate.opsForZSet()
        .reverseRangeWithScores("post:hot", 0, 999);
      if (ranked == null || ranked.isEmpty()) {
        return;
      }
      //每个ranked的分数都x0.8
      for (ZSetOperations.TypedTuple<String> tuple : ranked) {
        if (tuple.getValue() == null) {
          continue;
        }
        double score = tuple.getScore() * 0.8;
        if(score <1) {
          //从redis删除
          stringRedisTemplate.opsForZSet().remove("post:hot", tuple.getValue());
        }else{
         //新分数是原来的分数x0.8
         stringRedisTemplate.opsForZSet().add("post:hot", tuple.getValue(), score);
        }
      }
    } catch (Exception ex) {
      System.err.println("处理热榜数据失败: " + ex.getMessage());
    }
  }

  public List<PostDto> getHotPosts() {
    /**
     * 1.从redis获取榜单集合
     * 2.从mysql拼接集合数据到controller
     * 3.数据按照id顺序排序
     */
    Set<ZSetOperations.TypedTuple<String>> ranked = stringRedisTemplate.opsForZSet()
        .reverseRangeWithScores("post:hot", 0, 19);
    if (ranked == null || ranked.isEmpty()) {
      return new ArrayList<PostDto>();
    }
    List<String> postIdsList = new ArrayList<String>();
    Map<String, Double> scores = new HashMap<String, Double>();
    for (ZSetOperations.TypedTuple<String> tuple : ranked) {
      if (tuple.getValue() == null) {
        continue;
      }
      postIdsList.add(tuple.getValue());
      scores.put(tuple.getValue(), tuple.getScore());
    }
    if (postIdsList.isEmpty()) {
      return new ArrayList<PostDto>();
    }
    List<PostDto> posts = postService.getPostsByIds(postIdsList);
    Collections.sort(posts, (a, b) -> postIdsList.indexOf(a.getId().toString()) - postIdsList.indexOf(b.getId().toString()));
    for (PostDto post : posts) {
      post.setScore(scores.get(post.getId().toString()));
    }
    return posts;
  }

  public void refreshHotPosts() {
    /**
     * 1.清空redis的热榜数据
     * 2.从mysql读取热榜数据，并写入redis
     */
    try {
      stringRedisTemplate.delete("post:hot");
      List<Post> hotPosts = postMapper.selectList(null);
      for (Post post : hotPosts) {
        int likeCount = post.getLikeCount() == null ? 0 : post.getLikeCount();
        int commentCount = post.getCommentCount() == null ? 0 : post.getCommentCount();
        int favoriteCount = post.getFavoriteCount() == null ? 0 : post.getFavoriteCount();
        double score = likeCount * 2 + commentCount * 3 + favoriteCount * 4;
        if (score <= 0) {
          continue;
        }
        stringRedisTemplate.opsForZSet().add("post:hot", post.getId().toString(), score);
      }
    } catch (Exception ex) {
      System.err.println("刷新热榜数据失败: " + ex.getMessage());
    }
  }
}
