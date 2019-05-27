package com.auth.repository;


import java.util.Optional;
import com.auth.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    Optional <User> findByEmail(String email);
    User findByUsername(String username);
    Boolean existsByEmail(String email);

}
