package org.example.community.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CommentDto {
  private Long id;
  private Long userId;
  private String nickname;
  private String content;
  private Date createTime;
}
