package com.order_service.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.order_service.model.LimitOrder;
import com.order_service.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;


public class Sender {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

    @Value("${kafka.topic}")
    private String topic;
    @Value("${kafka.topic.limit}")
    private String topicLimit;


    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;
    @Autowired
    private KafkaTemplate<String, LimitOrder> kafkaTemplateLimit;



    public void send(Order newOrder) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Message<Order> message = MessageBuilder
                .withPayload(newOrder)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        kafkaTemplate.send(message);
        LOGGER.info("sending order:'{}'", newOrder.getId());
        LOGGER.info("sending order:'{}'", newOrder.getTime());
    }


    public void sendLimit(LimitOrder newOrder) {
        Message<LimitOrder> message = MessageBuilder
                .withPayload(newOrder)
                .setHeader(KafkaHeaders.TOPIC, topicLimit)
                .build();

        kafkaTemplateLimit.send(message);
        LOGGER.info("sending order:'{}'", newOrder.getId());
    }
}