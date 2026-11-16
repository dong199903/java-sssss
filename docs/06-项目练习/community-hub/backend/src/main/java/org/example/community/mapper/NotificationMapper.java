package org.example.community.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.community.entity.Notification;

import java.util.List;

@Mapper
public interface NotificationMapper {

    int insertIgnoreBatch(@Param("list") List<Notification> list);

    List<Notification> listUnread(@Param("userId") Long userId);

    Notification selectById(@Param("id") Long id);

    int markRead(@Param("id") Long id, @Param("userId") Long userId);
}
