package com.price_service.model;


import lombok.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

@Getter
@Setter
@Builder
public class Price {
    private BigDecimal value;
    private Currency currency;
}
