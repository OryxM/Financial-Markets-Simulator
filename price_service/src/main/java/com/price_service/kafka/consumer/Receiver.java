package com.price_service.kafka.consumer;

import com.price_service.kafka.producer.Sender;
import com.price_service.model.*;
import com.price_service.repository.AccountRepository;
import com.price_service.repository.AssetRepository;
import com.price_service.repository.OrderRepository;
import com.price_service.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Service
public class Receiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    AssetRepository assetRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    Sender sender;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @KafkaListener(topics = "${kafka.topic.receiver.filledOrders}")
    public void listen(@Payload String orderId) {
        log.info("order {}  filled", orderId);
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
           if (order.getOrderType() == OrderType.LIMIT && order.getTransactionType() == TransactionType.BUY) {
               List<LimitOrder> bidList =orderRepository.findBid(new ObjectId(order.getAsset().getId()));
               if (!bidList.isEmpty()) {
                   log.info("Old Bid{}  ", order.getAsset().getBid());
                   order.getAsset().setBid(Price.builder().value(bidList.get(0).getLimitPrice()).currency(Currency.getInstance(Locale.US)).build());
                   log.info("New Bid{}  ", order.getAsset().getBid());
                   log.info("BID{}  ", bidList.get(0).getLimitPrice());
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
    @KafkaListener(topics = "${kafka.topic.receiver.transactions}")
    public void listenTransaction(@Payload String transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        log.info("transaction {}  ", transactionId);
        if (transaction.isPresent()) {

            this.messagingTemplate.convertAndSend("/topic/"+transaction.get().getAccountId().toHexString(), transaction.get());
            Optional<Account> account = accountRepository.findById(transaction.get().getAccountId().toHexString());
            account.get().updateAccountMetrics(transaction.get().getPrice(),
                    transaction.get().getCommission(),
                    transaction.get().getOrder().getTransactionType());
            accountRepository.save(account.get());
            if (transaction.get().getOrder().getAsset().getPrice().peekFirst().getValue()!=transaction.get().getPrice()) {
                transaction.get().getOrder().getAsset().updatePrice(Price.builder().value(transaction.get().getPrice()).currency(Currency.getInstance(Locale.US)).build());
                assetRepository.save(transaction.get().getOrder().getAsset());
            }
            List<StopLossOrder> stopLossOrders = orderRepository.findStopLossOrders(new ObjectId(transaction.get().getOrder().getAsset().getId()),
                    transaction.get().getPrice());
            if (!stopLossOrders.isEmpty()){
                log.info("StopSize{}  ", stopLossOrders.size());
                for(StopLossOrder stopLossOrder : stopLossOrders){
                    Order order = new Order(new ObjectId(),
                            stopLossOrder.getAccountId(),
                            stopLossOrder.getAsset(),
                            stopLossOrder.getTransactionType(),
                            stopLossOrder.getQuantity(),
                            stopLossOrder.getFilled(),
                            stopLossOrder.getOrderType(),
                            stopLossOrder.getDuration(),
                            ZonedDateTime.now(),
                            stopLossOrder.getState());
                    sender.send(order);

                }
            }
        }



    }

}