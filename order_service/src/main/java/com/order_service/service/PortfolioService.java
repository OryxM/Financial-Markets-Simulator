package com.order_service.service;

import com.order_service.message.request.AccountRequest;
import com.order_service.model.*;
import com.order_service.kafka.producer.Sender;
import com.order_service.message.request.OrderRequest;
import com.order_service.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

import static com.order_service.model.TransactionType.BUY;
import static java.math.BigDecimal.valueOf;


@Service
@Slf4j
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
        log.info(new Date().toString());
    }
    public Account createAccount(AccountRequest accountRequest) throws NoSuchElementException {
        Account account=Account.builder().balance(accountRequest.getBalance())
                                         .currency(Currency.getInstance(accountRequest.getCurrency()))
                                         .accountValue(accountRequest.getBalance())
                                         .build();
        accountRepository.save(account);
        User user=(User)userRepository.findById(accountRequest.getUserId()).orElseThrow(() ->
                        new NoSuchElementException("User Not Found with id : " + accountRequest.getUserId()));
        user.getAccounts().add(account);
        userRepository.save(user);
        return account;
    }

    public boolean createOrder(OrderRequest orderRequest){
        long totalVolume = 0;
        List<Transaction> transactions = transactionRepository.findByAccountId(new ObjectId(orderRequest.getAccountId()));
        for (Transaction transaction : transactions){
            if (transaction.getOrder().getTransactionType() == BUY
                    && transaction.getOrder().getAsset().getSymbol() == orderRequest.getAssetSymbol())
            totalVolume+=transaction.getVolume();
            else totalVolume-=transaction.getVolume();
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
        List<Transaction> transactions =transactionRepository.findByAccountId(new ObjectId(accountId));
        BigDecimal currentPrice;
        for (Transaction transaction : transactions ){
            if (transaction.getOrder().getTransactionType() == BUY)
            currentPrice = assetRepository.findBySymbol(transaction.getOrder().getAsset().getSymbol()).getBid().getValue();
            else
                currentPrice = assetRepository.findBySymbol(transaction.getOrder().getAsset().getSymbol()).getAsk().getValue();
            transaction.setCurrentPrice(currentPrice);
        }
        return transactions;
      //  return  transactionRepository.findByAccountId(new ObjectId(accountId));
    }

    public List<Account> getAccounts(String userId) {
        return (userRepository.findById(userId).get().getAccounts());
    }
    public Account updateAccountMetrics(String accountId){
        BigDecimal stockValue=BigDecimal.ZERO;
       List<Transaction> transactions=transactionRepository.findByAccountId(new ObjectId(accountId));
       transactions.forEach(transaction -> {
           if (transaction.getOrder().getTransactionType() == BUY)
               stockValue.add(transaction.getOrder().getAsset().getBid().getValue().multiply(valueOf(transaction.getVolume())));
           else
               stockValue.subtract(transaction.getOrder().getAsset().getAsk().getValue().multiply(valueOf(transaction.getVolume())));
       });

     Account account = (Account)accountRepository.findById(accountId).orElseThrow(() ->
             new NoSuchElementException("Account Not Found with id : " + accountId));
     account.setAccountValue(account.getBalance().add(stockValue));
     log.info("account value: {}",account.getAccountValue());
        log.info("balance: {}",account.getBalance());
     accountRepository.save(account);
     return account;
    }
    public String getMailByAccount(String accountId){
        return userRepository.findByAccountId(new ObjectId(accountId)).getEmail();
    }
}

