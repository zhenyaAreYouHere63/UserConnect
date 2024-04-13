package org.task.authenticify.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.task.authenticify.entity.token.TokenPair;
import org.task.authenticify.request.ChangePasswordRequest;
import org.task.authenticify.dto.user.UserDto;
import org.task.authenticify.service.UserService;
import java.util.List;

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
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public void confirmEmail(@PathVariable @NotBlank String token) {
        userService.confirmEmail(token);
    }

    @GetMapping("/api/auth/send/reset-password-email/{email}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public void ResetPasswordEmail(@PathVariable @NotBlank String email) {
        userService.changePasswordEmail(email);
    }

    @PostMapping("api/auth/change-password")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
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
