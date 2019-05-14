package com.price_service.controller;
import java.util.Map;
import java.util.Optional;

import com.price_service.model.Transaction;
import com.price_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import com.google.gson.Gson;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    TransactionRepository transactionRepository;
    @MessageMapping("/message")
    @SendTo("/topic/reply")
    public String processMessageFromClient(@Payload String message) throws Exception {
        String name = new Gson().fromJson(message, Map.class).get("name").toString();
        return "Bonjour" +name;
    }
    @KafkaListener(topics = "${kafka.topic.receiver.transactions}")
    public void listenTransaction(@Payload String transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
            String userId= transaction.get().getUserId().toHexString();
            this.messagingTemplate.convertAndSend("/topic/"+userId, transactionId);

    }


    @MessageExceptionHandler
    public String handleException(Throwable exception) {
        messagingTemplate.convertAndSend("/errors", exception.getMessage());
        return exception.getMessage();
    }

}


