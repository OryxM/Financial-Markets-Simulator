package com.order_service.repository;

import java.util.Optional;

import com.order_service.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    Optional <User> findByEmail(String email);
    User findByUsername(String username);
    Boolean existsByEmail(String email);
}
