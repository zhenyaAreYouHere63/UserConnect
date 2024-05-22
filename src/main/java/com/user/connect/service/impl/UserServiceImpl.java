package com.user.connect.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import com.user.connect.entity.token.TokenPair;
import com.user.connect.exception.external.EmailNotVerifiedException;
import com.user.connect.exception.external.InvalidCredentialsException;
import com.user.connect.dto.ChangePasswordRequestDto;
import com.user.connect.dto.user.UserDto;
import com.user.connect.entity.user.User;
import com.user.connect.mail.EmailService;
import com.user.connect.mapper.UserMapper;
import com.user.connect.repository.UserRepository;
import com.user.connect.exception.external.UserNotFoundException;
import com.user.connect.service.JwtService;
import com.user.connect.service.UserService;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final HttpServletRequest servlet;

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final JwtService jwtService;

    @Override
    public TokenPair registerUser(UserDto userDto) {

        if(userRepository.findUserByEmail(userDto.email()).isPresent()) {
            throw new InvalidCredentialsException("Invalid email [" + userDto.email() + "]. Given email is already taken.");
        }

        String hashedPassword = BCrypt.hashpw(userDto.password(), BCrypt.gensalt());

        User userToSave = userMapper.mapUserDtoToUser(userDto);
        userToSave.setPassword(hashedPassword);

        String link = "www.google.com";
        emailService.sendEmailVerification(userToSave.getEmail(), "Confirm email address", "Please, follow the link to verify your email address: " + link);
        userToSave.setIsEmailVerified(true);

        User savedUser = userRepository.save(userToSave);

        TokenPair tokenPair = jwtService.issueTokenPair(savedUser);
        log.info("Registering new user with email: {}", userDto.email());;
        return tokenPair;
    }

    @Override
    public void resendEmailConfirmation(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with [" + email + "] not found"));

        String link = "www.google.com";
        emailService.sendEmailVerification(email,
                "Confirm email address", "Please, follow the link to verify your email address: " + link);

        user.setIsEmailVerified(true);
        log.info("Resending email confirmation for user with email: {}", email);
    }

    @Override
    public void confirmEmail(String token) {
        String email = jwtService.parseAccessToken(token).tokenClaims().email();

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EmailNotVerifiedException("Email not confirmed"));
        log.info("Confirming email for user with token: {}", token);
    }

    @Override
    public void changePasswordEmail(String email) {
        userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with [" + email +  "] not found"));

        String link = "www.google.com";
        emailService.sendEmailVerification(email,
                "Change password email", "Please, follow the link to change your password: " + link);
        log.info("Change password via email for user with email: {}", email);
    }

    @Override
    public void changePassword(ChangePasswordRequestDto request) {
        String token = request.token();
        String email = jwtService.parseAccessToken(token).tokenClaims().email();

        User foundUser = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with [" + email +  "] not found"));

        String hashedPassword = BCrypt.hashpw(request.newPassword(), BCrypt.gensalt());
        foundUser.setPassword(hashedPassword);

        userRepository.save(foundUser);
        log.info("Changing password for user with email: {}", foundUser.getEmail());
    }

    @Override
    public String ping() {
        return "pong";
    }

    @Override
    public UserDto getUserProfile() {
        String token = servlet.getHeader("Authorization");

        String[] tokenParts = token.split("\\s+");
        String jwtToken = tokenParts[1];

        String email = jwtService.parseAccessToken(jwtToken).tokenClaims().email();
        log.info("Retrieving user profile");
        return userRepository.findUserByEmail(email)
                .map(userMapper::mapUserToUserDto)
                .orElseThrow(() -> new UserNotFoundException("User with " + email + " not found"));
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Retrieving all users");
        return userRepository.findAll()
                .stream().map(userMapper::mapUserToUserDto)
                .collect(Collectors.toList());
    }
}
