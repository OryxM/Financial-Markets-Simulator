package com.order_service.controller;


import com.order_service.message.request.AccountRequest;
import com.order_service.message.response.OrderResponse;
import com.order_service.model.Asset;
import com.order_service.message.request.OrderRequest;
import com.order_service.service.PortfolioService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController

public class PortfolioController {
    @Autowired
    PortfolioService portfolioService;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @RequestMapping(value = "/assets")
    public List<Asset> getAssets() {
        return (portfolioService.getAssets());
    }

    @RequestMapping(value = "/orders/{accountId}")
    public ResponseEntity<Object> getOrders(@PathVariable String accountId){
        return new ResponseEntity<>(portfolioService.getOrders(accountId), HttpStatus.OK);
    }
    @RequestMapping(value = "/transactions/{accountId}")
    public ResponseEntity<Object> getTransactions(@PathVariable String accountId){
        return new ResponseEntity<>(portfolioService.getTransactions(accountId), HttpStatus.OK);
    }
    @RequestMapping(value = "/refresh/{accountId}")
    public ResponseEntity<Object> updateMetrics(@PathVariable String accountId) {
        return new ResponseEntity<>(portfolioService.updateAccountMetrics(accountId), HttpStatus.OK);
    }
    @RequestMapping(value = "/accounts/{userId}")
    public ResponseEntity<Object> getAccounts(@PathVariable String userId){
        return new ResponseEntity<>(portfolioService.getAccounts(userId), HttpStatus.OK);
    }
    @PostMapping(value = "/create-order")
    public ResponseEntity<Object> createOrder(@RequestBody OrderRequest orderRequest) {
        if (portfolioService.createOrder(orderRequest)) {
            return new ResponseEntity<>(new OrderResponse("order created"), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(
                    new OrderResponse("insufficient quantity"),
                    HttpStatus.BAD_REQUEST);
        }

    }
    @RequestMapping(value = "/mailByAccount/{accountId}")
    public ResponseEntity<Object> getMailByAccount(@PathVariable String accountId){
        return new ResponseEntity<>(portfolioService.getMailByAccount(accountId), HttpStatus.OK);
    }
    @PostMapping(value = "/create-account")
    public ResponseEntity<Object> createAccount(@RequestBody AccountRequest accountRequest) {
        this.messagingTemplate.convertAndSend("/topic/"+accountRequest.getUserId(), portfolioService.createAccount(accountRequest));
        return new ResponseEntity<>(new OrderResponse("account created"), HttpStatus.OK);
    }
}
