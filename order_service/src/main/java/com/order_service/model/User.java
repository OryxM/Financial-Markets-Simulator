package com.order_service.model;

import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "users")
@Getter @Setter
public class User {

    @Id
    @Field("_id")
    private ObjectId id;

    @Indexed(unique = true, direction = IndexDirection.DESCENDING, dropDups = true)
    private String email;
    private String password;
    private String username;
    @DBRef
    private Set<Role> roles;
    @DBRef
    private List<Account> accounts;

    public String getId() { return id.toHexString(); }

}