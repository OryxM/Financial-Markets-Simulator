package com.price_service.repository;

import com.price_service.model.*;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigDecimal;
import java.util.List;


public interface OrderRepository extends MongoRepository<Order,String> {
    @Query(value=" {$and: [ {'asset.$id': ?0}, {'orderType':'LIMIT'},{'state':'PENDING'},{'transactionType':'SELL'}]}",fields="{'limitPrice':1}",sort = "{'limitPrice':1}")
    List<LimitOrder> findAsk(ObjectId id);

    @Query(value=" { $and: [ {'asset.$id': ?0}, {'orderType':'LIMIT'},{'state':'PENDING'},{'transactionType':'BUY'}]}",fields="{'limitPrice':1}",sort = "{'limitPrice':-1}")
    List<LimitOrder> findBid(ObjectId id);
   @Query("{$or:[{'asset.$id': ?0,'state': 'PENDING','orderType':'STOP','transactionType':'SELL','stopPrice':{'$lte':?1}},{'asset.$id': ?0,'state': 'PENDING','orderType':'STOP','transactionType':'BUY','stopPrice':{'$gte':?1}}]}")
    List<StopLossOrder> findStopLossOrders(ObjectId assetId,BigDecimal transactionPrice);
}
