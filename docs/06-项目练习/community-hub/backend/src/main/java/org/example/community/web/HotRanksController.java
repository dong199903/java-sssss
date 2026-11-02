package org.example.community.web;

import org.example.community.service.HotRanksService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.community.common.ApiResponse;
import org.example.community.dto.PostDto;
import java.util.List;
@RestController
@RequestMapping("/api/ranks")
public class HotRanksController {
  @Autowired
  private HotRanksService hotRanksService;

  @GetMapping("/hot")
  public ApiResponse<List<PostDto>> getHotRanks() {
    return ApiResponse.ok(hotRanksService.getHotRanks());
  }
  
}
