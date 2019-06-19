package com.matching_service.service;
import com.matching_service.kafka.producer.Sender;
import com.matching_service.model.*;
import com.matching_service.repository.LimitOrderRepository;
import com.matching_service.repository.MarketOrderRepository;
import com.matching_service.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.ZonedDateTime;


@Service
@Slf4j
public class MatchingService {
    @Autowired
    LimitOrderRepository limitOrderRepository;
    @Autowired
    MarketOrderRepository marketOrderRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    Sender sender;

    public OrderBook buildOrderBook(Order order){
        OrderBook orderBook= new OrderBook();
        orderBook.setAsset(order.getAsset());
        if (order instanceof LimitOrder) {
            switch (order.getTransactionType()) {
                case BUY:
                    orderBook.setOrders(limitOrderRepository.findByTransactionAndAssetBuyLimit(order.oppositeTransaction(),
                            order.getAsset().getId(),
                            OrderType.LIMIT,
                            Status.PENDING,
                            ((LimitOrder) order).getLimitPrice()));
                    break;
                case SELL:
                    orderBook.setOrders(limitOrderRepository.findByTransactionAndAssetSellLimit(order.oppositeTransaction(),
                            order.getAsset().getId(),
                            OrderType.LIMIT,
                            Status.PENDING,
                            ((LimitOrder) order).getLimitPrice()));
                    break;

            }
        }
        else{
            orderBook.setOrders(limitOrderRepository.findByTransactionAndAsset(order.oppositeTransaction(),
                    order.getAsset().getId(),
                    OrderType.LIMIT,
                    Status.PENDING));
        }
        orderBook.sortOrders();



//*********************************************
        for (LimitOrder limitOrder : orderBook.getOrders()) {
            log.info("price : '{}'-time : '{}'-quantity : '{}'",limitOrder.getLimitPrice(),
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

    public void fillMarketOrder(OrderBook orderBook,Order order){


        if (order.getQuantity() <=  orderBook.totalVolume() && ((canBeImmediatelyFilled(orderBook, order) && order.getDuration()==Duration.FOK ) || order.getDuration()==Duration.IOC))

        {
            for (LimitOrder matchingOrder :orderBook.getOrders()) {
                if (order.notFilledQuantity()>= matchingOrder.notFilledQuantity())
                {
                    Transaction transactionLimit = new Transaction(new ObjectId(),
                            matchingOrder.getAccountId(),
                            matchingOrder,
                            matchingOrder.getLimitPrice(),
                            matchingOrder.notFilledQuantity(),
                            ZonedDateTime.now(),
                            BigDecimal.valueOf(15));
                    transactionRepository.save(transactionLimit);
                    sender.sendTransaction(transactionLimit.getId());

                    Transaction transaction = new Transaction(new ObjectId(),
                            order.getAccountId(),
                            order,
                            matchingOrder.getLimitPrice(),
                            order.notFilledQuantity(),
                            ZonedDateTime.now(),
                            BigDecimal.valueOf(15));
                    transactionRepository.save(transaction);

                    sender.sendTransaction(transaction.getId());
                    order.setFilled(order.getFilled() + matchingOrder.notFilledQuantity());
                    matchingOrder.fillOrder();
                    limitOrderRepository.save(matchingOrder);
                    sender.sendOrder(matchingOrder.getId());
                }
                else{
                    switch (matchingOrder.getDuration()) {
                        case IOC:
                            matchingOrder.setFilled(matchingOrder.getFilled() + order.notFilledQuantity());
                            limitOrderRepository.save(matchingOrder);
                            Transaction transactionLimit = new Transaction(new ObjectId(),
                                    matchingOrder.getAccountId(),
                                    matchingOrder,
                                    matchingOrder.getLimitPrice(),
                                    order.notFilledQuantity(),
                                    ZonedDateTime.now(),
                                    BigDecimal.valueOf(15));
                            transactionRepository.save(transactionLimit);
                            sender.sendTransaction(transactionLimit.getId());
                            Transaction transaction = new Transaction(new ObjectId(),
                                    order.getAccountId(),
                                    order,
                                    matchingOrder.getLimitPrice(),
                                    order.notFilledQuantity(),
                                    ZonedDateTime.now(),
                                    BigDecimal.valueOf(15));
                            transactionRepository.save(transaction);
                            sender.sendTransaction(transaction.getId());

                            order.setFilled(order.getQuantity());
                            sender.sendOrder(order.getId());
                            break;
                        default:
                            break;

                    }
                }
                if (order.getQuantity() == order.getFilled()){
                    order.fillOrder();
                    marketOrderRepository.save(order);
                    sender.sendOrder(order.getId());
                    return;
                }
            }
        }

        if ((order.getQuantity() <=  orderBook.totalVolume() && !canBeImmediatelyFilled(orderBook, order))|| (order.getQuantity() >  orderBook.totalVolume() && order.getDuration()==Duration.FOK)) {
            order.setStatus(Status.KILLED);
            marketOrderRepository.save(order);
        }
        if (order.getQuantity() > orderBook.totalVolume() && order.getDuration()==Duration.IOC) {

            for (LimitOrder matchingOrder : orderBook.getOrders()) {
                if (matchingOrder.getDuration() == Duration.IOC || (matchingOrder.getDuration() != Duration.IOC && matchingOrder.notFilledQuantity() <= order.notFilledQuantity())) {
                    matchingOrder.fillOrder();

                    limitOrderRepository.save(matchingOrder);
                    sender.sendOrder(matchingOrder.getId());
                    Transaction transactionLimit = new Transaction(new ObjectId(),
                            matchingOrder.getAccountId(),
                            matchingOrder,
                            matchingOrder.getLimitPrice(),
                            matchingOrder.notFilledQuantity(),
                            ZonedDateTime.now(),
                            BigDecimal.valueOf(15));
                    transactionRepository.save(transactionLimit);
                    sender.sendTransaction(transactionLimit.getId());
                    Transaction transaction = new Transaction(new ObjectId(),
                            order.getAccountId(),
                            order,
                            matchingOrder.getLimitPrice(),
                            order.notFilledQuantity(),
                            ZonedDateTime.now(),
                            BigDecimal.valueOf(15));
                    transactionRepository.save(transaction);
                    sender.sendTransaction(transaction.getId());
                    order.setFilled(order.getFilled() + matchingOrder.notFilledQuantity());

                }
            }

            order.fillOrder();

            marketOrderRepository.save(order);
            sender.sendOrder(order.getId());

        }
    }





}