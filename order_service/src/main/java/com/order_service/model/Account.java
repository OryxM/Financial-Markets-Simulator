package com.order_service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import static java.math.BigDecimal.valueOf;

@Document(collection = "accounts")
@Getter @Setter
@Builder
public class Account {
    @Id
    @Field("_id")
    private ObjectId id;
    private BigDecimal balance ;
    private BigDecimal equity ;
    private Currency currency;



    public String getId() { return id.toHexString(); }

}
