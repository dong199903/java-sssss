package org.example.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

@Data
@TableName("post")
public class Post {
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;
  private Long communityId;
  private Long authorId;
  private String title;
  private String content;
  private BigDecimal latitude;
  private BigDecimal longitude;
  private String geohash;
  private Long userId;
  private Date createTime;
  private Date updateTime;
}
