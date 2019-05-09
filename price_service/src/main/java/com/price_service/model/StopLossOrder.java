package com.price_service.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

import java.math.BigDecimal;
@TypeAlias("stopOrder")

public class StopLossOrder extends Order{
    @Getter @Setter
    private BigDecimal stopPrice;
    public StopLossOrder(){
        this.orderType = OrderType.STOP;
    }

}
