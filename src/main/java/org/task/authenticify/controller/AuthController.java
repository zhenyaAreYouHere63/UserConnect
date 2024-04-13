package org.task.authenticify.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.task.authenticify.dto.auth.CredentialsDto;
import org.task.authenticify.entity.token.TokenPair;
import org.task.authenticify.service.AuthService;

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
