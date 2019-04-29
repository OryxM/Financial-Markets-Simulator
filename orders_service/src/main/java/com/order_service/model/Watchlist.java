package com.order_service.model;


import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;


@Document(collection = "watchlists")
@Getter
@Setter
public class Watchlist {
    @Id
    @Field("_id")
    private ObjectId id;
    private List<Asset> assets;



    public String getId() { return id.toHexString(); }
}

