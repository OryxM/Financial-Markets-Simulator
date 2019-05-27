package com.price_service.repository;



import com.price_service.model.Order;
import com.price_service.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction,String> {

    Transaction findTopByOrderByTimeDesc(Order order);
}
