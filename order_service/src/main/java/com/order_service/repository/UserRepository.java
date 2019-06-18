package com.order_service.repository;

import java.util.Optional;
import com.order_service.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {

    Optional <User> findByEmail(String email);
    User findByUsername(String username);
    Boolean existsByEmail(String email);
    @Query(value=" {'accounts.$id': ?0}")
    User findByAccountId(ObjectId accountId);

}
