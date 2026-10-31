package org.example.community.dto;
import lombok.Data;
import java.util.List;
import java.util.Date;

@Data
public class PostListDto {
  private List<PostDto> postList;
  private Long lastId;
  private Date lastCreateTime;

  public List<PostDto> getPostList() {
    return postList;
  }
  public void setPostList(List<PostDto> postList) {
    this.postList = postList;
  }
}
