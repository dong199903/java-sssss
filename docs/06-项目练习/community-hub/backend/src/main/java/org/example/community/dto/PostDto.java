package org.example.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
  private Long id;
  private Long communityId;
  private String title;
  private String content;
  private BigDecimal latitude;
  private BigDecimal longitude;
  private Long userId;
  private Date createTime;
  private Date updateTime;
}
