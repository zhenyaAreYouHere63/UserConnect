package com.user.connect.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.user.connect.dto.auth.CredentialsDto;
import com.user.connect.entity.token.TokenPair;
import com.user.connect.service.AuthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/auth/login")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenPair login(@Valid @RequestBody CredentialsDto credentialsDto) {
        return authService.login(credentialsDto);
    }

    @DeleteMapping("/api/auth/logout/{email}")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@PathVariable @NotBlank String email) {
        authService.logout(email);
    }
}
