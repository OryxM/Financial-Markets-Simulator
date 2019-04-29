package com.order_service.repository;

import com.order_service.model.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface AssetRepository extends MongoRepository<Asset, String> {
    Asset findBySymbol(String assetSymbol);

}
