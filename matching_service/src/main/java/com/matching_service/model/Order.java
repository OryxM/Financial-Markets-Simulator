package com.matching_service.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import lombok.ToString;
import lombok.Getter;
import lombok.Setter;

import static java.math.BigDecimal.valueOf;

@Document(collection = "orders")
@Getter @Setter
@ToString
public class Order {
    @Id
    @Field("_id")
    private ObjectId id;
    @DBRef
    private Asset asset;
    private TransactionType transactionType; // buy or sell
    private long quantity;
    private long filled = 0;
    protected OrderType orderType = OrderType.MARKET;
    private Duration duration;
    private ZonedDateTime time;
    private State state = State.PENDING;
    private ZonedDateTime executionTime;
    private BigDecimal executionPrice;
    private BigDecimal commission;

    public String getId() { return id.toHexString();}
    @JsonIgnore
    public ZonedDateTime getTime() {
        return this.time;
    }

    public TransactionType oppositeTransaction(){
     return  ((transactionType == TransactionType.SELL) ? TransactionType.BUY : TransactionType.SELL);
    }
    public long notFilledQuantity(){
        return quantity - filled ;
    }
    public void fillOrder(BigDecimal executionPrice){
        this.state = State.FILLED;
        this.filled = this.quantity;
        this.executionTime= ZonedDateTime.now();
        this.executionPrice= executionPrice;
        this.commission = valueOf(20);

    }
}
