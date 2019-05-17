package com.price_service.controller;
import java.time.ZonedDateTime;
import java.util.*;

import com.price_service.kafka.producer.Sender;
import com.price_service.model.*;
import com.price_service.repository.AccountRepository;
import com.price_service.repository.AssetRepository;
import com.price_service.repository.OrderRepository;
import com.price_service.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;


@Slf4j
@Controller
public class WebSocketController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AssetRepository assetRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    Sender sender;


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


    @MessageExceptionHandler
    public String handleException(Throwable exception) {
        messagingTemplate.convertAndSend("/errors", exception.getMessage());
        return exception.getMessage();
    }

}


