package com.matching_service.service;
import com.matching_service.model.*;
import com.matching_service.repository.LimitOrderRepository;
import com.matching_service.repository.MarketOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;


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


public boolean canBeImmediatelyFilled(OrderBook orderBook,Order order){
    Long canBeFilled = orderBook.getOrders().get(0).notFilledQuantity();
    Long canBePartiallyFilled = Long.valueOf(0);
    int i = 0;

    while (order.getQuantity() > canBeFilled && i < orderBook.getOrders().size()){
        i++;
            canBeFilled+=orderBook.getOrders().get(i).notFilledQuantity();

        }
     if (order.getQuantity() == canBeFilled) return true;

    for (int j=i;j< orderBook.getOrders().size();j++){
        if (orderBook.getOrders().get(j).getDuration()== Duration.IOC)
            canBePartiallyFilled+= orderBook.getOrders().get(j).notFilledQuantity();
    }
    if (order.getQuantity()-canBeFilled+orderBook.getOrders().get(i-1).notFilledQuantity()<=canBePartiallyFilled)
        return true;
    return false;
}

// check duration everytime
public void fillMarketOrder(OrderBook orderBook,Order order){
    BigDecimal executionPrice = valueOf(0);

    if (order.getQuantity() <=  orderBook.totalVolume() && ((canBeImmediatelyFilled(orderBook, order) && order.getDuration()==Duration.FOK ) || order.getDuration()==Duration.IOC))

    {
        for (LimitOrder matchingOrder :orderBook.getOrders()) {
            if (order.notFilledQuantity()>= matchingOrder.notFilledQuantity())
            { executionPrice = executionPrice.add(matchingOrder.getLimitPrice().multiply(valueOf(matchingOrder.notFilledQuantity())));
                order.setFilled(order.getFilled() + matchingOrder.notFilledQuantity());
                matchingOrder.fillOrder(matchingOrder.getLimitPrice());
                limitOrderRepository.save(matchingOrder);

            }
            else{
                switch (matchingOrder.getDuration()) {
                    case FOK:
                        break;
                    default:
                        matchingOrder.setFilled(matchingOrder.getFilled() + order.notFilledQuantity());
                        executionPrice = executionPrice.add(matchingOrder.getLimitPrice().multiply(valueOf(order.notFilledQuantity())));
                        limitOrderRepository.save(matchingOrder);
                        order.setFilled(order.getQuantity());
                        break;
                    //send event filled order

                }
            }
            if (order.getQuantity() == order.getFilled()){
                order.fillOrder(executionPrice.divide(valueOf(order.getFilled()), RoundingMode.HALF_UP));
                marketOrderRepository.save(order);
                //send event filled order
               return;
            }
        }
    }

    if ((order.getQuantity() <=  orderBook.totalVolume() && !canBeImmediatelyFilled(orderBook, order))|| (order.getQuantity() >  orderBook.totalVolume() && order.getDuration()==Duration.FOK)) {
        order.setState(State.KILLED);
        marketOrderRepository.save(order);
    }
    if (order.getQuantity() > orderBook.totalVolume() && order.getDuration()==Duration.IOC) {

        for (LimitOrder matchingOrder : orderBook.getOrders()) {
            if (matchingOrder.getDuration() == Duration.IOC || (matchingOrder.getDuration() == Duration.FOK && matchingOrder.notFilledQuantity() > order.notFilledQuantity())) {
                matchingOrder.fillOrder(matchingOrder.getLimitPrice());
                executionPrice = executionPrice.add(matchingOrder.getLimitPrice().multiply(valueOf(matchingOrder.notFilledQuantity())));
                limitOrderRepository.save(matchingOrder);
                order.setFilled(order.getFilled() + matchingOrder.notFilledQuantity());
                //send event filled order
            }
        }

            order.fillOrder(executionPrice.divide(valueOf(order.getFilled()),RoundingMode.HALF_UP));

        marketOrderRepository.save(order);

    }
        }



    public void fillLimitOrder(OrderBook orderBook,LimitOrder order){
        BigDecimal executionPrice = valueOf(0);


    }
    public void fillStopLossOrder(OrderBook orderBook,StopLossOrder order){
        BigDecimal executionPrice = valueOf(0);


    }



}
