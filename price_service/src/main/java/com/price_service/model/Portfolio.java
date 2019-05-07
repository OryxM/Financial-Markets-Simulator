package com.price_service.model;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;


@Document(collection = "portfolios")
@Getter @Setter
public class Portfolio {
    @Id
    @Field("_id")
    private ObjectId id;
    private Market market;
    private List<Order> orders;
    private List<Watchlist> watchlists;



}
