package com.order_service.message.request;


import com.order_service.model.Duration;
import com.order_service.model.TransactionType;
import com.order_service.model.OrderType;
import lombok.Getter;
import lombok.Setter;


import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Optional;

@Getter @Setter
public class OrderRequest {

    private String assetSymbol;
    private TransactionType transactionType;
    @NotBlank
    private long quantity;
    private OrderType orderType;
    private Optional<BigDecimal> limitPrice;
    private Optional<BigDecimal> stopPrice;
    private Duration duration;
    private String accountId;



}
