package org.example.community.web;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.example.community.dto.PostDto;
import org.example.community.common.ApiResponse;
import java.util.List;
import org.example.community.service.RankHotService;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/rank/hot")
public class RankHotController {
  @Autowired
  private RankHotService rankHotService;

  @GetMapping("/list")
  public ApiResponse<List<PostDto>> getHotPosts() {
    return ApiResponse.ok(rankHotService.getHotPosts());
  }

  @GetMapping("/refresh")
  public ApiResponse<String> refreshHotPosts() {
    rankHotService.refreshHotPosts();
    return ApiResponse.ok("处理热榜数据成功");
  }
}
