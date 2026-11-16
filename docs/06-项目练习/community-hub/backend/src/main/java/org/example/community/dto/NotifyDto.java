package org.example.community.dto;

import lombok.Data;

@Data
public class NotifyDto {
  private String eventId;
  private String eventType;
  private Long bizId;
  private Long receiverId;
  private String content;
}
