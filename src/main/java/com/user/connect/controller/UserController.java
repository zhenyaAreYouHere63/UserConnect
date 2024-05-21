package com.user.connect.controller;

import java.util.List;
import com.user.connect.dto.ChangePasswordRequestDto;
import com.user.connect.dto.user.UserDto;
import com.user.connect.entity.token.TokenPair;
import com.user.connect.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenPair registration(@Valid @RequestBody UserDto userDto) {
        return userService.registerUser(userDto);
    }

    @GetMapping("/api/auth/resend/email-confirmation/{email}")
    @ResponseStatus(HttpStatus.OK)
    public void resendEmailConfirmation(@PathVariable @NotBlank String email) {
        userService.resendEmailConfirmation(email);
    }

    @GetMapping("/api/auth/email-confirm/{token}")
    @ResponseStatus(HttpStatus.OK)
    public void confirmEmail(@PathVariable @NotBlank String token) {
        userService.confirmEmail(token);
    }

    @GetMapping("/api/auth/send/reset-password-email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public void ResetPasswordEmail(@PathVariable @NotBlank String email) {
        userService.changePasswordEmail(email);
    }

    @PostMapping("api/auth/change-password")
    @ResponseStatus(HttpStatus.CREATED)
    public void changePassword(@Valid @RequestBody ChangePasswordRequestDto request) {
        userService.changePassword(request);
    }

    @GetMapping("api/user/current-user")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getCurrentUser() {
        return userService.getUserProfile();
    }

    @GetMapping("api/user/all")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/ping")
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return userService.ping();
    }
}
