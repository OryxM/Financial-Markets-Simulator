package com.order_service.model;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
public class LimitOrder extends Order{
 @Getter @Setter
    private BigDecimal limitPrice;
 public LimitOrder(){
     this.orderType = OrderType.LIMIT;
 }

}
