package org.task.authenticify.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.task.authenticify.dto.auth.TokenPair;
import org.task.authenticify.exception.external.EmailNotVerifiedException;
import org.task.authenticify.exception.external.InvalidCredentialsException;
import org.task.authenticify.request.ChangePasswordRequest;
import org.task.authenticify.dto.user.UserDto;
import org.task.authenticify.entity.user.User;
import org.task.authenticify.mail.EmailService;
import org.task.authenticify.mapper.UserMapper;
import org.task.authenticify.repository.UserRepository;
import org.task.authenticify.exception.external.UserNotFoundException;
import org.task.authenticify.service.JwtService;
import org.task.authenticify.service.UserService;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final HttpServletRequest servlet;

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final JwtService jwtService;

    @Override
    public TokenPair registerUser(UserDto userDto) {

        if(userRepository.findUserByEmail(userDto.email()).isPresent()) {
            throw new InvalidCredentialsException("Invalid email [" + userDto.email() + "]. Given email is already taken" );
        }

        String hashedPassword = BCrypt.hashpw(userDto.password(), BCrypt.gensalt());

        User userToSave = userMapper.mapUserDtoToUser(userDto);
        userToSave.setPassword(hashedPassword);

        TokenPair tokenPair = jwtService.issueTokenPair(userToSave);

        String link = "www.google.com";
        emailService.sendEmailVerification(userToSave.getEmail(), "Confirm email address", "Please, follow the link to verify your email address: " + link);

        userToSave.setIsEmailVerified(true);
        userRepository.save(userToSave);

        return tokenPair;
    }

    @Override
    public void resendEmailConfirmation(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with " + email + " not found"));

        String link = "www.google.com";
        emailService.sendEmailVerification(email,
                "Confirm email address", "Please, follow the link to verify your email address: " + link);

        user.setIsEmailVerified(true);
    }

    @Override
    public void confirmEmail(String token) {
        String email = jwtService.parseAccessToken(token).tokenClaims().email();

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EmailNotVerifiedException("Email not confirmed"));
    }

    @Override
    public void changePasswordEmail(String email) {
        userRepository.findUserByEmail(email);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        String token = request.token();

        String email = jwtService.parseAccessToken(token).tokenClaims().email();
        User foundUser = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with " + email +  " not found"));

        String hashedPassword = BCrypt.hashpw(request.newPassword(), BCrypt.gensalt());

        foundUser.setPassword(hashedPassword);

        userRepository.save(foundUser);
    }

    @Override
    public String ping() {
        return "pong";
    }

    @Override
    public User getUserProfile() {
        String token = servlet.getHeader("Authorization");

        String[] tokenParts = token.split("\\s+");
        String jwtToken = tokenParts[1];

        String email = jwtService.parseAccessToken(jwtToken).tokenClaims().email();

        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with " + email + " not found"));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream().map(userMapper::mapUserToUserDto)
                .collect(Collectors.toList());
    }
}
