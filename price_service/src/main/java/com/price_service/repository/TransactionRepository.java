package com.price_service.repository;


import com.price_service.model.Asset;
import com.price_service.model.Order;
import com.price_service.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TransactionRepository extends MongoRepository<Transaction,String> {

    Transaction findTopByOrderByTimeDesc(Order order);
}
