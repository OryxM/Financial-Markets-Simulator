package com.order_service.service;

import com.order_service.message.request.AccountRequest;
import com.order_service.model.*;
import com.order_service.kafka.producer.Sender;
import com.order_service.message.request.OrderRequest;
import com.order_service.repository.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;


@Service
public class PortfolioService {

    @Autowired
    AssetRepository assetRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    Sender sender;
    private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioService.class);
    public List<Asset> getAssets(){
       return(assetRepository.findAll());
    }

    public void createMarketOrder(OrderRequest orderRequest, Order order) {
        Asset asset = assetRepository.findBySymbol(orderRequest.getAssetSymbol());
        order.setAsset(asset);
        order.setAccountId(new ObjectId(orderRequest.getAccountId()));
        order.setTransactionType(orderRequest.getTransactionType());
        order.setQuantity(orderRequest.getQuantity());
        order.setDuration(orderRequest.getDuration());
        order.setTime(ZonedDateTime.now());
        LOGGER.info(new Date().toString());
    }
    public Account createAccount(AccountRequest accountRequest){
        Account account=Account.builder().balance(accountRequest.getBalance())
                                         .currency(Currency.getInstance(accountRequest.getCurrency()))
                                         .accountValue(accountRequest.getBalance())
                                         .build();
        accountRepository.save(account);
        Optional<User> user=userRepository.findById(accountRequest.getUserId());
        user.get().getAccounts().add(account);
        userRepository.save(user.get());
        return account;
    }

    public boolean createOrder(OrderRequest orderRequest){
        long totalVolume = 0;
        List<Transaction> transactions = transactionRepository.findByAccountId(new ObjectId(orderRequest.getAccountId()));
        for (Transaction transaction : transactions){
            totalVolume+=transaction.getVolume();
        }
        if (!(totalVolume < orderRequest.getQuantity() && orderRequest.getTransactionType() == TransactionType.SELL)) {
            switch (orderRequest.getOrderType()) {

                case LIMIT:
                    LimitOrder limitOrder = new LimitOrder();
                    createMarketOrder(orderRequest, limitOrder);
                    limitOrder.setLimitPrice(orderRequest.getLimitPrice().get());

                    orderRepository.save(limitOrder);
                    sender.sendLimit(limitOrder);
                    return true;
                case STOP:
                    StopLossOrder stopLossOrder = new StopLossOrder();
                    createMarketOrder(orderRequest, stopLossOrder);
                    stopLossOrder.setStopPrice(orderRequest.getStopPrice().get());
                    orderRepository.save(stopLossOrder);
                    return true;

                default:
                    Order marketOrder = new Order();
                    marketOrder.setId(new ObjectId());
                    createMarketOrder(orderRequest, marketOrder);
                    orderRepository.save(marketOrder);
                    sender.send(marketOrder);
                    return true;

            }
        }
     return false;

    }

    public List<Order> getOrders(String accountId){
        return(orderRepository.findByAccountId(new ObjectId(accountId)));
    }
    public List<Transaction> getTransactions(String accountId){
        return(transactionRepository.findByAccountId(new ObjectId(accountId)));
    }

    public List<Account> getAccounts(String userId) {
        //for (Account account :userRepository.findById(userId).get().getAccounts()) {
//            account.updateAccountValue();
  //      }
        return (userRepository.findById(userId).get().getAccounts());
    }
}

