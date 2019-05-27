package com.auth.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "roles")
@Getter @Setter
public class Role {

    @Id
    @Field("_id")
    private ObjectId id;

    @Indexed(unique = true, direction = IndexDirection.DESCENDING, dropDups = true)
    private String role;

}

