package com.order_service.controller;


import com.order_service.model.Asset;
import com.order_service.message.request.OrderRequest;
import com.order_service.service.PortfolioService;
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

    @RequestMapping(value = "/orders")
    public ResponseEntity<Object> getOrders() {
        return new ResponseEntity<>(portfolioService.getOrders(), HttpStatus.OK);
    }
    @PostMapping(value = "/create-order")
    public ResponseEntity<Object> createOrder(@RequestBody OrderRequest orderRequest)
    {
        return new ResponseEntity<>(portfolioService.createOrder(orderRequest), HttpStatus.OK);
    }
}
