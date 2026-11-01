package org.example.community.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.community.service.PostService;
import org.example.community.common.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.example.community.dto.CommentDto;
import org.example.community.dto.PostDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import java.util.Date;
import org.example.community.dto.PostListDto;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/posts")
public class PostController {
  @Autowired
  private PostService postService;

  /**获取小区帖子 */
  @GetMapping("/list")
  public ApiResponse<PostListDto> getPostList(HttpServletRequest request, @RequestParam Map<String, String> params) {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      return ApiResponse.error(null, "未登录");
    }
    String lastTimeText = params.get("lastCreateTime");
    String lastIdText = params.get("lastId");
    Date lastCreateTime = (lastTimeText == null || lastTimeText.isEmpty()) ? null : new Date(Long.parseLong(lastTimeText));
    Long cursorId = (lastIdText == null || lastIdText.isEmpty()) ? null : Long.parseLong(lastIdText);

    List<PostDto> postList = postService.getPostList(Integer.parseInt(params.get("communityId")), lastCreateTime, cursorId);
    if (postList.isEmpty()) {
      return ApiResponse.error(null, "没有更多帖子");
    }
    Long lastId = postList.get(postList.size() - 1).getId();
    Date lastCreateTimes = postList.get(postList.size() - 1).getCreateTime();
    PostListDto postListDto = new PostListDto();
    postListDto.setPostList(postList);
    postListDto.setLastId(lastId);
    postListDto.setLastCreateTime(lastCreateTimes);
    return ApiResponse.ok(postListDto);
  }

  /**附近帖子 */
  @GetMapping("/nearby")
  public ApiResponse<PostListDto> getNearbyPosts(@RequestParam Map<String, String> params) {
    String latText = params.get("latitude");
    String lngText = params.get("longitude");
    if (latText == null || latText.isEmpty() || lngText == null || lngText.isEmpty()) {
      return ApiResponse.error(null, "经纬度不能为空");
    }
    String lastTimeText = params.get("lastCreateTime");
    String lastIdText = params.get("lastId");
    Date lastCreateTime = (lastTimeText == null || lastTimeText.isEmpty()) ? null : new Date(Long.parseLong(lastTimeText));
    Long cursorId = (lastIdText == null || lastIdText.isEmpty()) ? null : Long.parseLong(lastIdText);
    List<PostDto> postList = postService.getNearbyPosts(Double.parseDouble(latText), Double.parseDouble(lngText), lastCreateTime, cursorId);
    PostListDto postListDto = new PostListDto();
    postListDto.setPostList(postList);
    if (!postList.isEmpty()) {
      postListDto.setLastId(postList.get(postList.size() - 1).getId());
      postListDto.setLastCreateTime(postList.get(postList.size() - 1).getCreateTime());
    }
    return ApiResponse.ok(postListDto);
  }

  /**帖子详情 */
  @GetMapping("/{id}")
  public ApiResponse<PostDto> getPost(@PathVariable Long id) {
    PostDto post = postService.getPost(id);
    if (post == null) {
      return ApiResponse.error(null, "帖子不存在");
    }
    return ApiResponse.ok(post);
  }

  /* 发帖接口 */
  @PostMapping("/post")
  public ApiResponse<String> postArticle(HttpServletRequest request, @RequestBody PostDto postDto) {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      return ApiResponse.error("未登录");
    }
    /**
     * 发帖接口
     * 1. 获取用户信息
     * 2. 获取帖子内容
     * 3. 保存帖子
     * 4. 返回结果
     */
    int result = postService.postArticle(userId, postDto);
    if (result > 0) {
      return ApiResponse.ok("success");
    } else {
      return ApiResponse.error("发帖失败");
    }
  }

  /**更新帖子 */
  @PutMapping("/{id}")
  public ApiResponse<String> updatePost(@PathVariable Long id, @RequestBody PostDto postDto,
          HttpServletRequest request, HttpServletResponse response) {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      return ApiResponse.error("未登录");
    }
    int result = postService.updatePost(userId, id, postDto);
    if (result == 1) {
      return ApiResponse.ok("success");
    }
    if (result == 2) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return new ApiResponse<>(403, "无权修改", null);
    }
    if (result == 3) {
      return ApiResponse.error("标题和正文不能为空");
    }
    return ApiResponse.error("帖子不存在");
  }

  /**删除帖子 */
  @DeleteMapping("/{id}")
  public ApiResponse<String> deletePost(@PathVariable Long id, HttpServletRequest request,
          HttpServletResponse response) {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      return ApiResponse.error("未登录");
    }
    int result = postService.deletePost(userId, id);
    if (result == 1) {
      return ApiResponse.ok("success");
    }
    if (result == 2) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return new ApiResponse<>(403, "无权删除", null);
    }
    return ApiResponse.error("帖子不存在");
  }

  /*点赞帖子 */
  @PostMapping("/{id}/likes")
  public ApiResponse<String> likePost(@PathVariable Long id, HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      return ApiResponse.error("未登录");
    }
    int result = postService.likePost(userId, id);
    if (result == 1) {
      return ApiResponse.ok("success");
    }
    if (result == -1) {
      return ApiResponse.error("已点赞");
    }
    if (result == 2) {
      return ApiResponse.error("帖子不存在");
    }
    return ApiResponse.error("点赞失败");
  }

  /**取消点赞 */
  @DeleteMapping("/{id}/likes")
  public ApiResponse<String> unlikePost(@PathVariable Long id, HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      return ApiResponse.error("未登录");
    }
    try {
      int result = postService.unlikePost(userId, id);
      if (result == 1) {
        return ApiResponse.ok("success");
      }
      if (result == -1) {
        return ApiResponse.error("未点赞");
      }
      if (result == 2) {
        return ApiResponse.error("帖子不存在");
      }
      return ApiResponse.error("取消点赞失败");
    } catch (IllegalStateException ex) {
      return ApiResponse.error("取消点赞失败");
    }
  }

  /**收藏帖子 */
  @PostMapping("/{id}/favorites")
  public ApiResponse<String> favoritePost(@PathVariable Long id, HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      return ApiResponse.error("未登录");
    }
    int result = postService.favoritePost(userId, id);
    if (result == 1) {
      return ApiResponse.ok("success");
    }
    if (result == -1) {
      return ApiResponse.error("已收藏");
    }
    if (result == 2) {
      return ApiResponse.error("帖子不存在");
    }
    return ApiResponse.error("收藏失败");
  }

  /**取消收藏 */
  @DeleteMapping("/{id}/favorites")
  public ApiResponse<String> unfavoritePost(@PathVariable Long id, HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      return ApiResponse.error("未登录");
    }
    try {
      int result = postService.unfavoritePost(userId, id);
      if (result == 1) {
        return ApiResponse.ok("success");
      }
      if (result == -1) {
        return ApiResponse.error("未收藏");
      }
      if (result == 2) {
        return ApiResponse.error("帖子不存在");
      }
      return ApiResponse.error("取消收藏失败");
    } catch (IllegalStateException ex) {
      return ApiResponse.error("取消收藏失败");
    }
  }

  /**评论列表 */
  @GetMapping("/{id}/comments")
  public ApiResponse<List<CommentDto>> getComments(@PathVariable Long id, HttpServletRequest request,
          @RequestParam Map<String, String> params) {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      return ApiResponse.error(null, "未登录");
    }
    String lastTimeText = params.get("lastCreateTime");
    String lastIdText = params.get("lastId");
    Date lastCreateTime = (lastTimeText == null || lastTimeText.isEmpty()) ? null : new Date(Long.parseLong(lastTimeText));
    Long cursorId = (lastIdText == null || lastIdText.isEmpty()) ? null : Long.parseLong(lastIdText);
    List<CommentDto> comments = postService.getComments(id, lastCreateTime, cursorId);
    if (comments == null) {
      return ApiResponse.error(null, "帖子不存在");
    }
    return ApiResponse.ok(comments);
  }

  /**发表评论 */
  @PostMapping("/{id}/comments")
  public ApiResponse<String> addComment(@PathVariable Long id, @RequestBody CommentDto commentDto,
          HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      return ApiResponse.error("未登录");
    }
    try {
      int result = postService.addComment(userId, id, commentDto.getContent());
      if (result == 1) {
        return ApiResponse.ok("success");
      }
      if (result == 2) {
        return ApiResponse.error("帖子不存在");
      }
      if (result == 3) {
        return ApiResponse.error("评论内容不能为空");
      }
      if (result == 4) {
        return ApiResponse.error("评论过长");
      }
      return ApiResponse.error("评论失败");
    } catch (IllegalStateException ex) {
      return ApiResponse.error("评论失败");
    }
  }
}
