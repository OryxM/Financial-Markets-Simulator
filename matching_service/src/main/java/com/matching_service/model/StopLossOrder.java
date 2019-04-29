package com.matching_service.model;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
public class StopLossOrder extends Order{
    @Getter @Setter
    private BigDecimal stopPrice;
    public StopLossOrder(){
        this.orderType = OrderType.STOP;
    }

}
