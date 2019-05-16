package com.order_service.repository;

import com.order_service.model.Order;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order,String> {
List<Order> findByAccountId(ObjectId accountId);
}
