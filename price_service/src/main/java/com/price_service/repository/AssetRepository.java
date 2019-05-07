package com.price_service.repository;

import com.price_service.model.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AssetRepository extends MongoRepository<Asset,String> {
}
