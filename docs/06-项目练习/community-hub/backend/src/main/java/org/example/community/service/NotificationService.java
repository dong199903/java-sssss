package org.example.community.service;

import org.example.community.entity.Notification;
import org.example.community.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    public List<Notification> listUnread(Long userId) {
        return notificationMapper.listUnread(userId);
    }

    public int markRead(Long userId, Long id) {
        Notification row = notificationMapper.selectById(id);
        if (row == null) {
            return 2;
        }
        if (!userId.equals(row.getReceiverId())) {
            return 3;
        }
        notificationMapper.markRead(id, userId);
        return 1;
    }
}
