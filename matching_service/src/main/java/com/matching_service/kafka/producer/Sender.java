package com.matching_service.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;



public class Sender {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

    @Value("${kafka.topic.sender}")
    private String topic;


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String orderId) {
        LOGGER.info("order filled : {}", orderId);
        kafkaTemplate.send(topic, orderId);
    }
}