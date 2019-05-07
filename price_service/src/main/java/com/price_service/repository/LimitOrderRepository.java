package com.price_service.repository;

import com.price_service.model.LimitOrder;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface LimitOrderRepository extends MongoRepository<LimitOrder,String> {
@Query("{ 'asset.$id': ?0,'orderType':'LIMIT', 'state':'PENDING', 'transactionType':'BUY'},{'limitPrice':1}")
List<BigDecimal> findBid(ObjectId id);

@Query("{ 'asset.$id': ?0,'orderType':'LIMIT', 'state':'PENDING', 'transactionType':'SELL'},{'limitPrice':1}")
    List <BigDecimal>findAsk( ObjectId id);
        }