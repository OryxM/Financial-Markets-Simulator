package com.order_service.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.Currency;


@Getter
@Setter
@Builder

public class Price {
    private BigDecimal value;
    private Currency currency;
}
