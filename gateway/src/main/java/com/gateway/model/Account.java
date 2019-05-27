package com.gateway.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.DecimalMax;
import java.math.BigDecimal;
import java.util.Currency;

@Document(collection = "accounts")
@Getter @Setter
@Builder
public class Account {
    @Id
    @Field("_id")
    private ObjectId id;
    private BigDecimal balance ;
    private BigDecimal accountValue ;
    private Currency currency;



    public String getId() { return id.toHexString(); }

}
