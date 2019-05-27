package com.order_service.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;


@Document(collection = "orders")
@Data
@Builder
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

    private Duration duration;
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime time ;
    private Status status = Status.PENDING;
    protected OrderType orderType = OrderType.MARKET;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public ZonedDateTime getTime() {
        return this.time;
    }
    public String getId() { return id.toHexString();}



}
