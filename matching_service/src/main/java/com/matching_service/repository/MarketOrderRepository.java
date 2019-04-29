package com.matching_service.repository;

import com.matching_service.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MarketOrderRepository extends MongoRepository<Order,String> {
}
