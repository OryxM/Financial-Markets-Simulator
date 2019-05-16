package com.order_service.message.request;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Currency;

@Getter @Setter
public class AccountRequest {
    private String userId;
    @NotBlank
    @DecimalMax("${MAX_BALANCE}")
    private BigDecimal balance;
    private String currency;

}
