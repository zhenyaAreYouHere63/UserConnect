package org.task.webapplication.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.task.webapplication.request.ChangePasswordRequest;
import org.task.webapplication.request.LoginRequest;
import org.task.webapplication.dto.UserDto;
import org.task.webapplication.entity.User;
import org.task.webapplication.jwt.JwtProvider;
import org.task.webapplication.mail.EmailService;
import org.task.webapplication.mapper.UserMapper;
import org.task.webapplication.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final HttpServletRequest servlet;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final UserRepository repository;

    private final EmailService emailService;

    private final JwtProvider jwtProvider;

    @Override
    public String registerUser(UserDto userDto) {
        User userToSave = userMapper.mapUserDtoToUser(userDto);

        String token = jwtProvider.generateToken(userToSave.getEmail());

        String link = "www.google.com";
        emailService.sendEmailVerification(userToSave.getEmail(), "Confirm email address", "Please, follow the link to verify your email address: " + link);

        userToSave.setIsEmailVerified(true);
        repository.save(userToSave);

        return token;
    }

    @Override
    public String loginUser(LoginRequest loginRequest) {

        User foundUser = repository.findUserByEmail(loginRequest.email())
                .orElseThrow(() -> new UserNotFoundException("User with " + loginRequest.email() + " not found"));

        if (!foundUser.getPassword().equals(loginRequest.password())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        if (!foundUser.getIsEmailVerified()) {
            throw new EmailNotVerifiedException("Email not verified. Please verify your email first");
        }

        return jwtProvider.generateToken(foundUser.getEmail());
    }

    @Override
    public void resendEmailConfirmation(String email) {
        User user = repository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with " + email + " not found"));

        if (!user.getIsEmailVerified()) {
            throw new EmailNotVerifiedException("Email not verified. Please verify your email first");
        }

        String link = "www.google.com";
        emailService.sendEmailVerification(email,
                "Confirm email address", "Please, follow the link to verify your email address: " + link);
    }

    @Override
    public void confirmEmail(String token) {
        String email = jwtProvider.getSubject(token);

        User user = repository.findUserByEmail(email)
                .orElseThrow(() -> new EmailNotVerifiedException("Email not confirmed"));
    }

    @Override
    public void changePasswordEmail(String email) {
        repository.findUserByEmail(email);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        String token = request.token();

        String email = jwtProvider.getSubject(token);
        User foundUser = repository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with " + email +  " not found"));

        foundUser.setPassword(request.newPassword());
        repository.save(foundUser);
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

        String email = jwtProvider.getSubject(jwtToken);

        return repository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with " + email + " not found"));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll()
                .stream().map(userMapper::mapUserToUserDto)
                .collect(Collectors.toList());
    }
}
