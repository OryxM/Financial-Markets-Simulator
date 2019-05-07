package com.matching_service.model;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.matching_service.Configuration.CustomZonedDateTimeDeserializer;
import com.sun.xml.internal.ws.util.Constants;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    private ZonedDateTime time;
    private State state = State.PENDING;



    public String getId() { return id.toHexString();}

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")

    public ZonedDateTime getTime() {
        return this.time;
    }

    public TransactionType oppositeTransaction(){
     return  ((transactionType == TransactionType.SELL) ? TransactionType.BUY : TransactionType.SELL);
    }
    public long notFilledQuantity(){
        return quantity - filled ;
    }
    public void fillOrder(){
        this.state = State.FILLED;
        this.filled = this.quantity;



    }
}
