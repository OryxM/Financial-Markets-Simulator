package com.price_service.kafka.consumer;

import com.price_service.kafka.producer.Sender;
import com.price_service.model.*;
import com.price_service.repository.AssetRepository;
import com.price_service.repository.OrderRepository;
import com.price_service.repository.TransactionRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

@Service
public class Receiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    AssetRepository assetRepository;

    @KafkaListener(topics = "${kafka.topic.receiver.filledOrders}")
    public void listen(@Payload String orderId) {
        LOGGER.info("order {}  filled", orderId);
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
           if (order.getOrderType() == OrderType.LIMIT && order.getTransactionType() == TransactionType.BUY) {
               List<LimitOrder> bidList =orderRepository.findBid(new ObjectId(order.getAsset().getId()));
               if (!bidList.isEmpty()) {
                   LOGGER.info("Old Bid{}  ", order.getAsset().getBid());
                   order.getAsset().setBid(Price.builder().value(bidList.get(0).getLimitPrice()).currency(Currency.getInstance(Locale.US)).build());
                   LOGGER.info("New Bid{}  ", order.getAsset().getBid());
                   LOGGER.info("BID{}  ", bidList.get(0).getLimitPrice());
                   assetRepository.save(order.getAsset());
               }
            }
            if (order.getOrderType() == OrderType.LIMIT && order.getTransactionType() == TransactionType.SELL) {
               List<LimitOrder> askList =orderRepository.findAsk(new ObjectId(order.getAsset().getId()));
               if (!askList.isEmpty()) {
                   order.getAsset().setAsk(Price.builder().value(askList.get(0).getLimitPrice()).currency(Currency.getInstance(Locale.US)).build());
                   LOGGER.info("ASK{}  ", askList.get(0).getLimitPrice());
                   assetRepository.save(order.getAsset());
               }
            }



        }
    }


}