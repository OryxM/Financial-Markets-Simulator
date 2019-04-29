package com.order_service.message.response;

import com.order_service.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
@Getter @Setter
public class AuthenticationResponse {

    private String token;
    private Optional<User> user;

    public AuthenticationResponse(Optional<User> user , String token) {
        this.user = user;
        this.token=token;
    }

}
