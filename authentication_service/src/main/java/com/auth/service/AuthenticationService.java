package com.auth.service;


import com.auth.message.request.LoginForm;
import com.auth.message.request.SignUpForm;
import com.auth.message.response.AuthenticationResponse;
import com.auth.model.Role;
import com.auth.model.User;

import com.auth.repository.AccountRepository;
import com.auth.repository.RoleRepository;
import com.auth.repository.UserRepository;
import com.auth.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthenticationService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;
public AuthenticationResponse signin(LoginForm loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);
    // return all user credentials+ ID
    Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());
    // generate a json web token
    String token =jwtProvider.generateJwtToken(authentication);
   return(new AuthenticationResponse(user,token));
}
public Boolean signup(SignUpForm signUpRequest){
    // registration fails if email address already exists
    if(userRepository.existsByEmail(signUpRequest.getEmail())) {
        return false;
    }

    // Create user's account
    User user = new User();
    //Encode password
    user.setPassword(encoder.encode(signUpRequest.getPassword()));
    // set credentials
    user.setEmail(signUpRequest.getEmail());
    user.setUsername(signUpRequest.getUsername());
    Set<Role> roles = new HashSet<>();
    Role userRole= roleRepository.findByRole("USER");
    roles.add(userRole);
    user.setRoles(roles);
    userRepository.save(user);
    return true;
}
}
