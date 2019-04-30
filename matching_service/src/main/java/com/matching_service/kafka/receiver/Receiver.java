package com.matching_service.kafka.receiver;

import com.matching_service.model.LimitOrder;
import com.matching_service.model.Order;
import com.matching_service.model.OrderBook;
import com.matching_service.model.StopLossOrder;
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

        @KafkaListener(topics = "${kafka.topic}", containerFactory="kafkaListenerContainerFactory")
        public void receive(@Payload Order newOrder, @Headers MessageHeaders headers) {
            LOGGER.info("receiving order:'{}'", newOrder.getId());
            OrderBook orderBook =  matchingService.buildOrderBook(newOrder);
            //LOGGER.info(orderBook.getOrders().isEmpty() == true ? "Empty":"notEmpty");
            //LOGGER.info(Long.toString(orderBook.totalVolume()));
            if (newOrder instanceof LimitOrder){

            }
            else if (newOrder instanceof StopLossOrder){

            }
            else{
                LOGGER.info("fok:{}",matchingService.canBeImmediatelyFilled(orderBook,newOrder));
                matchingService.fillMarketOrder(orderBook,newOrder);
            }


            LOGGER.info(newOrder.getState().toString());



        }
    }