package com.order_service.controller;


import javax.validation.Valid;

import com.order_service.message.request.LoginForm;
import com.order_service.message.request.SignUpForm;
import com.order_service.message.response.JwtResponse;
import com.order_service.service.AuthenticationService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/fms/auth")
public class AuthRestAPIs {

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

        return ResponseEntity.ok(new JwtResponse(authenticationService.signin(loginRequest)));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {

        // registration fails if email address already exists
        if(!authenticationService.signup(signUpRequest)) {
            return new ResponseEntity<String>("Fail -> Email is already in use!",
                    HttpStatus.BAD_REQUEST);
        }
        // Json response
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", true );

        return ResponseEntity.ok(jsonResponse);

    }
}
