package com.order_service.message.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import javax.validation.constraints.*;
@Getter @Setter
public class SignUpForm {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;



    @NotBlank
    @Size(max = 60)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

}
