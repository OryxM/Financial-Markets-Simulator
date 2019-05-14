package com.order_service.controller;


import com.order_service.message.response.OrderResponse;
import com.order_service.model.Asset;
import com.order_service.message.request.OrderRequest;
import com.order_service.service.PortfolioService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/fms/portfolio")
public class PortfolioController {
    @Autowired
    PortfolioService portfolioService;
    @RequestMapping(value = "/assets")
    public List<Asset> getAssets() {
        return (portfolioService.getAssets());
    }

    @RequestMapping(value = "/orders/{userId}")
    public ResponseEntity<Object> getOrders(@PathVariable String userId){
        return new ResponseEntity<>(portfolioService.getOrders(userId), HttpStatus.OK);
    }
    @RequestMapping(value = "/transactions/{userId}")
    public ResponseEntity<Object> getTransactions(@PathVariable String userId){
        return new ResponseEntity<>(portfolioService.getTransactions(userId), HttpStatus.OK);
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
}
