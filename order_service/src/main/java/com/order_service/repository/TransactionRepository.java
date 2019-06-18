package com.order_service.repository;

import com.order_service.model.Transaction;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction,String> {


    List<Transaction> findByAccountId(ObjectId accountId);

}
