package com.matching_service.kafka.receiver;

import com.matching_service.model.LimitOrder;
import com.matching_service.model.Order;
import com.matching_service.model.OrderBook;
import com.matching_service.service.MatchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;



public class Receiver {

        private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
        @Autowired
        MatchingService matchingService;



        @KafkaListener(topics = "${kafka.topic.receiver}", containerFactory="kafkaListenerContainerFactory")
        public void receive(@Payload Order newOrder, @Headers MessageHeaders headers) {
            LOGGER.info("receiving order: {} ", newOrder.getId());
            LOGGER.info("receiving order: {} ", newOrder.getTime());
            OrderBook orderBook =  matchingService.buildOrderBook(newOrder);
         if (orderBook.getOrders().isEmpty())
            LOGGER.info("No matching orders found");
         else {
             LOGGER.info("can be imm filled:{}", matchingService.canBeImmediatelyFilled(orderBook, newOrder));
             matchingService.fillMarketOrder(orderBook, newOrder);
             LOGGER.info(newOrder.getState().toString());
         }


        }


    @KafkaListener(topics = "${kafka.topic.receiver.limit}", containerFactory="kafkaListenerContainerFactoryLimit")
    public void receiveLimitOrder(@Payload LimitOrder newOrder, @Headers MessageHeaders headers) {

        LOGGER.info("receiving order: {} -- target price: {}", newOrder.getId(), newOrder.getLimitPrice());

        OrderBook orderBook =  matchingService.buildOrderBook(newOrder);
        if (orderBook.getOrders().isEmpty())
            LOGGER.info("No matching orders found");
        else {
            LOGGER.info("size: {}", orderBook.getOrders().size());
            LOGGER.info("can be imm filled:{}", matchingService.canBeImmediatelyFilled(orderBook, newOrder));
            matchingService.fillMarketOrder(orderBook, newOrder);
            LOGGER.info(newOrder.getState().toString());
        }

    }
    }