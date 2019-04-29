package com.order_service.kafka.producer;

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



    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;

    public void send(Order newOrder) {
        Message<Order> message = MessageBuilder
                .withPayload(newOrder)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        kafkaTemplate.send(message);
        LOGGER.info("sending order:'{}'", newOrder.getId());
    }
}