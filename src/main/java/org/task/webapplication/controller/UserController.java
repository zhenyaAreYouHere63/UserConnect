package org.task.webapplication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.task.webapplication.dto.UserDto;
import org.task.webapplication.entity.User;
import org.task.webapplication.service.UserService;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/auth/signup")
    public ResponseEntity<?> registration(@Valid @RequestBody UserDto userDto) {
        User user = userService.registerUser(userDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/auth/signup")
                .buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(location).body(user);
    }

    @GetMapping("/api/auth/resend/email-confirmation/{email}")
    public void resendEmailConfirmation(@PathVariable String email) {

    }

    @GetMapping("/api/auth/email-confirm/{token}")
    public void confirmEmail(@PathVariable String token) {

    }

    @PostMapping("api/auth/change-password")
    public void changePassword() {

    }

    @GetMapping("api/user/current-user")
    public void getCurrentUser() {

    }

    @GetMapping("api/user/all")
    public void getAllUsers() {

    }

    @GetMapping("/ping")
    public String ping() {
        return userService.ping();
    }
}
