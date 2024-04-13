package org.task.authenticify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.task.authenticify.dto.auth.CredentialsDto;
import org.task.authenticify.entity.token.TokenPair;
import org.task.authenticify.entity.user.User;
import org.task.authenticify.exception.external.InvalidCredentialsException;
import org.task.authenticify.exception.external.UserNotFoundException;
import org.task.authenticify.repository.UserRepository;
import org.task.authenticify.service.AuthService;
import org.task.authenticify.service.JwtService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenPair login(CredentialsDto credentialsDto) {
        User foundUser = userRepository.findUserByEmail(credentialsDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User with [" + credentialsDto.getEmail() + "] not found"));

        if(!passwordEncoder.matches(credentialsDto.getPassword(), foundUser.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        TokenPair tokenPair = jwtService.issueTokenPair(foundUser);
        log.info("User with email: [" + credentialsDto.getEmail() + "] logged in");
        return tokenPair;
    }

    @Override
    public void logout(String email) {
        userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with [" + email + "] not found"));

        jwtService.deleteAccessToken(email);
        jwtService.deleteRefreshToken(email);
        log.info("User with email: [" + email + "] logged out");
    }
}
