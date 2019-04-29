package com.matching_service.model;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
@Document(collection = "orders")
@ToString
public class LimitOrder extends Order{
 @Getter @Setter
    private BigDecimal limitPrice;
 public LimitOrder(){
     this.orderType = OrderType.LIMIT;
 }


}
