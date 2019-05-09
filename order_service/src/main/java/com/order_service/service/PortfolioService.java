package com.order_service.service;

import com.order_service.model.*;
import com.order_service.kafka.producer.Sender;
import com.order_service.message.request.OrderRequest;
import com.order_service.repository.AssetRepository;
import com.order_service.repository.OrderRepository;
import com.order_service.repository.TransactionRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;


@Service
public class PortfolioService {

    @Autowired
    AssetRepository assetRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    Sender sender;
    private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioService.class);
    public List<Asset> getAssets(){
       return(assetRepository.findAll());
    }

    public void createMarketOrder(OrderRequest orderRequest, Order order) {
        Asset asset = assetRepository.findBySymbol(orderRequest.getAssetSymbol());
        order.setAsset(asset);
        order.setUserId(new ObjectId(orderRequest.getUserId()));
        order.setTransactionType(orderRequest.getTransactionType());
        order.setQuantity(orderRequest.getQuantity());
        order.setDuration(orderRequest.getDuration());
        order.setTime(ZonedDateTime.now());
        LOGGER.info(new Date().toString());
    }

    public Order createOrder(OrderRequest orderRequest){
        switch(orderRequest.getOrderType()){

            case LIMIT:
                LimitOrder limitOrder = new LimitOrder();
                createMarketOrder(orderRequest,limitOrder);
                limitOrder.setLimitPrice(orderRequest.getLimitPrice().get());

                orderRepository.save(limitOrder);
                sender.sendLimit(limitOrder);
                return limitOrder;
            case STOP:
                StopLossOrder stopLossOrder =new StopLossOrder();
                createMarketOrder(orderRequest,stopLossOrder);
                stopLossOrder.setStopPrice(orderRequest.getStopPrice().get());
                orderRepository.save(stopLossOrder);
                return stopLossOrder;

            default:
                Order marketOrder = new Order();
                marketOrder.setId(new ObjectId());
                createMarketOrder(orderRequest,marketOrder);
                orderRepository.save(marketOrder);
                sender.send(marketOrder);
                return marketOrder;

        }

    }

    public List<Order> getOrders(String userId){
        return(orderRepository.findByUserId(new ObjectId(userId)));
    }
    public List<Transaction> getTransactions(String userId){

        return(transactionRepository.findByUserId(new ObjectId(userId)));
    }
}

