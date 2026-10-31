package org.example.community.service;

import java.util.List;
import java.util.ArrayList;
import org.example.community.common.GeoHash;
import org.example.community.dto.PostDto;
import org.springframework.stereotype.Service;
import org.example.community.mapper.PostMapper;
import org.example.community.entity.Post;
import java.util.Date;
import java.util.Map;

@Service
public class PostService {
  private final PostMapper postMapper;
  public PostService(PostMapper postMapper) {
    this.postMapper = postMapper;
  }
  public List<String> getAllPosts() {
    return new ArrayList<String>();
  }
  public List<PostDto> getPostList(int communityId, Date lastCreateTime, Long lastId) {
    return toPostDtos(postMapper.selectByCommunityId(communityId, 10, lastCreateTime, lastId));
  }

  public List<PostDto> getNearbyPosts(double latitude, double longitude, Date lastCreateTime, Long lastId) {
    String geohashStart = GeoHash.encode(latitude, longitude, 5);
    String geohashEnd = GeoHash.nextPrefix(geohashStart);
    return toPostDtos(postMapper.selectNearby(geohashStart, geohashEnd, 10, lastCreateTime, lastId));
  }

  private List<PostDto> toPostDtos(List<Post> posts) {
    List<PostDto> postDtos = new ArrayList<PostDto>();
    for (Post post : posts) {
      PostDto postDto = new PostDto();
      postDto.setId(post.getId());
      postDto.setTitle(post.getTitle());
      postDto.setContent(post.getContent());
      postDto.setCreateTime(post.getCreateTime());
      postDto.setUpdateTime(post.getUpdateTime());
      postDto.setLatitude(post.getLatitude());
      postDto.setLongitude(post.getLongitude());
      postDtos.add(postDto);
    }
    return postDtos;
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
}
