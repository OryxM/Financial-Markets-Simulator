package com.order_service.service;

import com.order_service.model.Account;
import com.order_service.message.request.LoginForm;
import com.order_service.message.request.SignUpForm;
import com.order_service.message.response.AuthenticationResponse;
import com.order_service.model.Role;
import com.order_service.model.User;
import com.order_service.repository.AccountRepository;
import com.order_service.repository.RoleRepository;
import com.order_service.repository.UserRepository;
import com.order_service.security.jwt.JwtProvider;
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
    Account account = new Account();
    accountRepository.save(account);
    user.setAccount(account);
    userRepository.save(user);
    return true;
}
}
