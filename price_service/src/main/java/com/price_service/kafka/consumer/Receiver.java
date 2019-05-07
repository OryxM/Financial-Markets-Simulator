package com.price_service.kafka.consumer;

import com.price_service.model.*;
import com.price_service.repository.AssetRepository;
import com.price_service.repository.LimitOrderRepository;
import com.price_service.repository.OrderRepository;
import com.price_service.repository.TransactionRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class Receiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    LimitOrderRepository limitOrderRepository;
    @Autowired
    AssetRepository assetRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @KafkaListener(topics = "${kafka.topic.receiver}")
    public void listen(@Payload String orderId) {
        LOGGER.info("order {}  filled", orderId);
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            Transaction transaction= transactionRepository.findTopByOrderByTimeDesc(order);
            LOGGER.info("transaction {} ", transaction.getId());
            // recheck this
            Asset asset = transaction.getOrder().getAsset();
            asset.updatePrice(transaction.getPrice());
            assetRepository.save(asset);
            //*******************
           if (order.getOrderType() == OrderType.LIMIT && order.getTransactionType() == TransactionType.BUY) {
                List<BigDecimal> bidList = limitOrderRepository.findBid(new ObjectId(order.getAsset().getId()));
                Collections.sort(bidList);
                //get max + update bid

            }
            if (order.getOrderType() == OrderType.LIMIT && order.getTransactionType() == TransactionType.SELL) {
                List<BigDecimal> askList = limitOrderRepository.findAsk(new ObjectId(order.getAsset().getId()));
                Collections.sort(askList);
                // get min+ update ask
            }



        }
    }
}