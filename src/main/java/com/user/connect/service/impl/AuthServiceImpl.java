package com.user.connect.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.user.connect.dto.auth.CredentialsDto;
import com.user.connect.entity.token.TokenPair;
import com.user.connect.entity.user.User;
import com.user.connect.exception.external.InvalidCredentialsException;
import com.user.connect.exception.external.UserNotFoundException;
import com.user.connect.repository.UserRepository;
import com.user.connect.service.AuthService;
import com.user.connect.service.JwtService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenPair login(CredentialsDto credentialsDto) {
        User foundUser = userRepository.findUserByEmail(credentialsDto.email())
                .orElseThrow(() -> new UserNotFoundException("User with [" + credentialsDto.email() + "] not found"));

        if(!passwordEncoder.matches(credentialsDto.password(), foundUser.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        TokenPair tokenPair = jwtService.issueTokenPair(foundUser);
        log.info("User with email: {} logged in", foundUser.getEmail());
        return tokenPair;
    }

    @Override
    public void logout(String email) {
        userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with [" + email + "] not found"));

        jwtService.deleteAccessToken(email);
        jwtService.deleteRefreshToken(email);
        log.info("User with email: {} logged out", email);
    }
}
