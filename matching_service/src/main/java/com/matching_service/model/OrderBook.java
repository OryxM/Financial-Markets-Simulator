package com.matching_service.model;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Getter @Setter

public class OrderBook {
    private Asset asset;
    protected List<LimitOrder> orders;

    public void sortOrders() {
        Collections.sort(this.orders, new Comparator() {
            public int compare(Object object1, Object object2) {

                BigDecimal price1 = ((LimitOrder) object1).getLimitPrice();
                BigDecimal price2 = ((LimitOrder) object2).getLimitPrice();
                int comparator = ((LimitOrder) object1).getTransactionType() == TransactionType.SELL ?
                        price1.compareTo(price2) : price2.compareTo(price1)  ;
                if (comparator != 0) {
                    return comparator;
                }
                ZonedDateTime timestamp1 =((LimitOrder) object1).getTime();
                ZonedDateTime timestamp2 =((LimitOrder) object2).getTime();
                return timestamp1.compareTo(timestamp2);
            }
        });
    }
    public long totalVolume()
    {   long volume = 0;
        for (Order order: orders) {
            volume += order.getQuantity() - order.getFilled();
        }

    return volume;
    }


}
