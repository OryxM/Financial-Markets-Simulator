package com.order_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.ArrayDeque;


@Document(collection = "assets")
@Getter @Setter
@NoArgsConstructor
public class Asset {
    @Id
    @Field("_id")
    private ObjectId id;
    // ticker symbol is unique
    @Indexed(unique = true, direction = IndexDirection.DESCENDING, dropDups = true)
    private String symbol;
    private ArrayDeque<Price> price;
    private Price bid;
    private Price ask;


    public String getId() { return id.toHexString(); }

}
