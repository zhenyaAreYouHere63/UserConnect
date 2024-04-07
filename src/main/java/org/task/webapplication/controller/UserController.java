package org.task.webapplication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.task.webapplication.entity.User;
import org.task.webapplication.request.ChangePasswordRequest;
import org.task.webapplication.request.LoginRequest;
import org.task.webapplication.dto.UserDto;
import org.task.webapplication.service.UserService;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public String registration(@Valid @RequestBody UserDto userDto) {
        return userService.registerUser(userDto);
    }

    @PostMapping("/api/auth/login")
    @ResponseStatus(HttpStatus.CREATED)
    public String login(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest);
    }

    @GetMapping("/api/auth/resend/email-confirmation/{email}")
    @ResponseStatus(HttpStatus.OK)
    public void resendEmailConfirmation(@PathVariable String email) {
        userService.resendEmailConfirmation(email);
    }

    @GetMapping("/api/auth/email-confirm/{token}")
    @ResponseStatus(HttpStatus.OK)
    public void confirmEmail(@PathVariable String token) {
        userService.confirmEmail(token);
    }

    @GetMapping("/api/auth/send/reset-password-email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public void ResetPasswordEmail(@PathVariable String email) {
        userService.changePasswordEmail(email);
    }

    @PostMapping("api/auth/change-password")
    @ResponseStatus(HttpStatus.CREATED)
    public void changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
    }

    @GetMapping("api/user/current-user")
    @ResponseStatus(HttpStatus.OK)
    public User getCurrentUser() {
        return userService.getUserProfile();
    }

    @GetMapping("api/user/all")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/ping")
    public String ping() {
        return userService.ping();
    }
}
