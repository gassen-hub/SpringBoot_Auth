package com.example.backend1.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend1.dto.LoginUserDto;
import com.example.backend1.dto.RegisterUserDto;
import com.example.backend1.dto.VerifyUserDTO;
import com.example.backend1.model.User;
import com.example.backend1.service.AuthentificationService;
import com.example.backend1.service.Jwtservice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")

@RequestMapping("/auth")
@RestController
@Tag(name = "Authentication")
public class AuthenticationController {
    private final Jwtservice jwtService;

    private final AuthentificationService authenticationService;

    public AuthenticationController(Jwtservice jwtService, AuthentificationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }


    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegisterUserDto request
    ) throws MessagingException {
        authenticationService.signup(request);
        return ResponseEntity.accepted().build();
    }


   @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(
            @RequestBody LoginUserDto request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }



    @GetMapping("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        authenticationService.activateAccount(token);
    }









}
