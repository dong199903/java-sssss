package org.example.community.mq;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.example.community.config.RabbitNotifyConfig;
import org.example.community.dto.NotifyDto;
import org.example.community.entity.Notification;
import org.example.community.mapper.NotificationMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class NotifyConsumer {

    private static final String RETRY_COUNT = "retry-count";
    private static final int MAX_ATTEMPTS = 3;

    private final ObjectMapper objectMapper;
    private final NotificationMapper notificationMapper;
    private final RabbitTemplate rabbitTemplate;

    public NotifyConsumer(ObjectMapper objectMapper, NotificationMapper notificationMapper,
            RabbitTemplate rabbitTemplate) {
        this.objectMapper = objectMapper;
        this.notificationMapper = notificationMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitNotifyConfig.NOTIFY_QUEUE)
    public void onBatch(List<Message> messages, Channel channel) throws IOException {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        List<Notification> rows = new ArrayList<>();
        try {
            for (Message message : messages) {
                NotifyDto event = objectMapper.readValue(message.getBody(), NotifyDto.class);
                Notification row = new Notification();
                row.setId(IdWorker.getId());
                row.setReceiverId(event.getReceiverId());
                row.setEventType(event.getEventType());
                row.setBizId(event.getBizId());
                row.setContent(event.getContent());
                row.setReadFlag(0);
                rows.add(row);
            }
            notificationMapper.insertIgnoreBatch(rows);
        } catch (Exception ex) {
            System.err.println("写入通知失败: " + ex.getMessage());
            for (Message message : messages) {
                retryOrDead(message, channel);
            }
            return;
        }
        for (Message message : messages) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    private void retryOrDead(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        int retry = retryCount(message);
        if (retry >= MAX_ATTEMPTS - 1) {
            channel.basicReject(tag, false);
            return;
        }
        MessageProperties props = new MessageProperties();
        props.setContentType(message.getMessageProperties().getContentType());
        props.setContentEncoding(message.getMessageProperties().getContentEncoding());
        props.getHeaders().putAll(message.getMessageProperties().getHeaders());
        props.setHeader(RETRY_COUNT, retry + 1);
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        if (routingKey == null || routingKey.isBlank()) {
            routingKey = "notify.favorite";
        }
        rabbitTemplate.send(RabbitNotifyConfig.TOPIC_EXCHANGE, routingKey, new Message(message.getBody(), props));
        channel.basicAck(tag, false);
    }

    private int retryCount(Message message) {
        Object value = message.getMessageProperties().getHeader(RETRY_COUNT);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return 0;
    }
}
