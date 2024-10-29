package com.example.backend1.service;
import com.example.backend1.controller.LoginResponse;
import com.example.backend1.dto.LoginUserDto;
import com.example.backend1.dto.RegisterUserDto;
import com.example.backend1.model.Token;
import com.example.backend1.model.User;
import com.example.backend1.repo.RoleRepo;
import com.example.backend1.repo.TokenRepo;
import com.example.backend1.repo.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthentificationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailServiceClass emailService;
    private final RoleRepo roleRepo ;
    private final TokenRepo tokenRepo ;

    private final Jwtservice jwtService;




    private void sendVerificationEmail(User user) { //TODO: Update with company logo
        String verificationCode = generateAndSaveActivationToken(user);

        String subject = "Account Verification";
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }

    public void signup(RegisterUserDto input) {
        var userRole  = roleRepo.findByName("USER").orElseThrow(()->new IllegalStateException("ROLE USER WAS NOT ITINIALIZED")) ;
        var user = User.builder()
                .username(input.getUsername())

                .email(input.getEmail())
                .password(passwordEncoder.encode(input.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
         userRepository.save(user);
        sendVerificationEmail(user);

    }
    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepo.findByToken(token)
                // todo exception has to be defined
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendVerificationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepo.save(savedToken);
    }

    public LoginResponse authenticate(LoginUserDto request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.getFullname());

        var jwtToken = jwtService.generateToken(claims, (User) auth.getPrincipal());
        return LoginResponse.builder()
                .token(jwtToken)
                .build();
    }
    private String generateAndSaveActivationToken(User user) {
      // Generate a token
      String generatedToken = generateActivationCode(6);
      var token = Token.builder()
              .token(generatedToken)
              .createdAt(LocalDateTime.now())
              .expiresAt(LocalDateTime.now().plusMinutes(15))
              .user(user)
              .build();
      tokenRepo.save(token);

      return generatedToken;
  }
    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }


}
