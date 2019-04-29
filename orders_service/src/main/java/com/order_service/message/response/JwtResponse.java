package com.order_service.message.response;

import com.order_service.model.User;
import com.order_service.security.jwt.JwtProvider;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
@Getter
@Setter
public class JwtResponse {
    private static final JwtProvider jwtProvider= new JwtProvider();
    private String token;
    private String type = "Bearer";
    private Optional<User> user;


    public JwtResponse(AuthenticationResponse authResponse) {
        this.token = authResponse.getToken();
        this.user= authResponse.getUser();
    }


}