package com.example.backend1.dto;


import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {
@Email(message = "Email is not formated")
    private String email ;
    private String password ;
    private String username ;
}