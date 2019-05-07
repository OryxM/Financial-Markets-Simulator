package com.price_service.repository;

import com.price_service.model.Order;

import org.springframework.data.mongodb.repository.MongoRepository;



public interface OrderRepository extends MongoRepository<Order,String> {

}