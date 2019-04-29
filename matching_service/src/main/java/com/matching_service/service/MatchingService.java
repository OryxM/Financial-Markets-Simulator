package com.matching_service.service;
import com.matching_service.model.*;
import com.matching_service.repository.LimitOrderRepository;
import com.matching_service.repository.MarketOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


import static java.math.BigDecimal.valueOf;


@Service
public class MatchingService {
    @Autowired
    LimitOrderRepository limitOrderRepository;
    @Autowired
    MarketOrderRepository marketOrderRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingService.class);

public OrderBook buildOrderBook(Order order){
OrderBook orderBook= new OrderBook();
orderBook.setAsset(order.getAsset());
orderBook.setOrders(limitOrderRepository.findByTransactionAndAsset(order.oppositeTransaction(),
                                                              order.getAsset().getId(),
                                                              OrderType.LIMIT,
                                                               State.PENDING));
orderBook.sortOrders();
//*********************************************
for (LimitOrder limitOrder : orderBook.getOrders()) {
    LOGGER.info("price : '{}'-time : '{}'-vol : '{}'",limitOrder.getLimitPrice(),
                                                        limitOrder.getTime().toString(),
                                                limitOrder.getQuantity());
}
//*********************************************
return orderBook;
}

public void attemptFillMarketOrder(OrderBook orderBook,Order order){
    BigDecimal executionPrice = valueOf(0);

    if (order.getQuantity() <=  orderBook.totalVolume())
    {
        for (LimitOrder matchingOrder :orderBook.getOrders()) {
            if (order.notFilledQuantity()>= matchingOrder.notFilledQuantity())
            { executionPrice = executionPrice.add(matchingOrder.getLimitPrice().multiply(valueOf(matchingOrder.notFilledQuantity())));
                order.setFilled(order.getFilled() + matchingOrder.notFilledQuantity());
                matchingOrder.fillOrder(matchingOrder.getLimitPrice());
                limitOrderRepository.save(matchingOrder);

            }
            else{
                matchingOrder.setFilled(matchingOrder.getFilled() + order.notFilledQuantity());
                executionPrice = executionPrice.add(matchingOrder.getLimitPrice().multiply(valueOf(order.notFilledQuantity())));
                limitOrderRepository.save(matchingOrder);
                order.setFilled(order.getQuantity());
                //send event filled order
            }
            if (order.getQuantity() == order.getFilled()){
                order.fillOrder(executionPrice.divide(valueOf(order.getFilled())));
                marketOrderRepository.save(order);
                //send event filled order
               return;
            }
        }
    }
        switch (order.getDuration())
        {
            case FOK:
                {
                order.setState(State.CANCELLED);
                marketOrderRepository.save(order);
                    //send event cancelled order
                break;
                }
            case IOC:
            {
                for (LimitOrder matchingOrder :orderBook.getOrders()){
                    matchingOrder.fillOrder(matchingOrder.getLimitPrice());
                    executionPrice = executionPrice.add(matchingOrder.getLimitPrice().multiply(valueOf(matchingOrder.notFilledQuantity())));
                    limitOrderRepository.save(matchingOrder);
                    //send event filled order
                }

                if(! orderBook.getOrders().isEmpty()){
                    order.setFilled(orderBook.totalVolume());
                    order.fillOrder(executionPrice.divide(valueOf(order.getFilled())));
                }
                marketOrderRepository.save(order);
                break;
            }

            default:
            {
                marketOrderRepository.save(order);
                break;
            }
        }

    }



}
