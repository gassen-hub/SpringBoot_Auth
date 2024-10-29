package com.example.backend1.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginUserDto {
@Email(message = "Email is not formated ")
@NotBlank(message = "Email is mandatory")
@NotEmpty(message = "Email is mandatory")
    private String email ;
    @NotBlank(message = "password is mandatory")
    @NotEmpty(message = "password is mandatory")
    private String password ;

}
