package com.order_service.model;
import lombok.*;
import org.springframework.data.annotation.TypeAlias;

import java.math.BigDecimal;

@Data
@TypeAlias("limitOrder")
@AllArgsConstructor
public class LimitOrder extends Order{

    private BigDecimal limitPrice;
 public LimitOrder(){
     this.orderType = OrderType.LIMIT;
 }

}
