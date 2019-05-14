package com.matching_service.repository;

import com.matching_service.model.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AssetRepository extends MongoRepository<Asset,String> {
}
