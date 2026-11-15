package org.example.community.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitNotifyConfig {

    public static final String TOPIC_EXCHANGE = "community.topic";
    public static final String NOTIFY_QUEUE = "notify.queue";
    public static final String NOTIFY_BINDING = "notify.#";
    public static final String DLX = "notify.dlx";
    public static final String DLQ = "notify.dlq";
    public static final String DEAD_ROUTING_KEY = "notify.dead";

    @Bean
    public TopicExchange communityTopic() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public Queue notifyQueue() {
        return QueueBuilder.durable(NOTIFY_QUEUE)
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey(DEAD_ROUTING_KEY)
                .withArgument("x-queue-type", "classic")
                .build();
    }

    @Bean
    public Binding notifyBinding() {
        return BindingBuilder.bind(notifyQueue()).to(communityTopic()).with(NOTIFY_BINDING);
    }

    @Bean
    public TopicExchange notifyDlx() {
        return new TopicExchange(DLX, true, false);
    }

    @Bean
    public Queue notifyDlq() {
        return QueueBuilder.durable(DLQ)
                .withArgument("x-queue-type", "classic")
                .build();
    }

    @Bean
    public Binding deadBinding() {
        return BindingBuilder.bind(notifyDlq()).to(notifyDlx()).with(DEAD_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jacksonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, MessageConverter jacksonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonMessageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(100);
        factory.setBatchListener(true);
        factory.setConsumerBatchEnabled(true);
        factory.setBatchSize(500);
        factory.setReceiveTimeout(1000L);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
