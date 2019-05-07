package com.matching_service.repository;

import com.matching_service.model.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface LimitOrderRepository extends MongoRepository<LimitOrder,String> {
    @Query("{ 'transactionType': ?0, 'asset.$id': ?1, 'orderType': ?2, 'state': ?3 }")
    List<LimitOrder> findByTransactionAndAsset(TransactionType transactionType, ObjectId id, OrderType type, State state);


    @Query("{ 'transactionType': ?0, 'asset.$id': ?1, 'orderType': ?2, 'state': ?3, 'limitPrice':{'$lt':?4}}")
    List<LimitOrder> findByTransactionAndAssetBuyLimit(TransactionType transactionType, ObjectId id, OrderType type, State state, BigDecimal price);

    @Query("{ 'transactionType': ?0, 'asset.$id': ?1, 'orderType': ?2, 'state': ?3, 'limitPrice':{'$gt':?4}}")
    List<LimitOrder> findByTransactionAndAssetSellLimit(TransactionType transactionType, ObjectId id, OrderType type, State state, BigDecimal price);

}

