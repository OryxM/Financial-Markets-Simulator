package com.price_service.model;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

import java.math.BigDecimal;

@TypeAlias("limitOrder")

public class LimitOrder extends Order{
 @Getter @Setter
 private BigDecimal limitPrice;
 public LimitOrder(){
     this.orderType = OrderType.LIMIT;
 }

}
