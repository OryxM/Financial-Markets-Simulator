package com.price_service.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "orders")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @Field("_id")
    private ObjectId id;
    private ObjectId accountId;
    @DBRef
    private Asset asset;
    private TransactionType transactionType; // buy or sell
    private long quantity;
    private long filled = 0;
    protected OrderType orderType = OrderType.MARKET;
    private Duration duration;
    private ZonedDateTime time;
    private State state = State.PENDING;


    public String getId() { return id.toHexString();}

}