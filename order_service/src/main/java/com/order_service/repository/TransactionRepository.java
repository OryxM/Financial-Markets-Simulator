package com.order_service.repository;

import com.order_service.model.Transaction;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction,String> {
    @Query("{'userId':?0}")
    List<Transaction> findByUserId(ObjectId userId);
}
