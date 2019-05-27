package com.auth.repository;


import com.auth.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface AccountRepository extends MongoRepository<Account, String> {

}
