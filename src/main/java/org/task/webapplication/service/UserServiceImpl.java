package org.task.webapplication.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.task.webapplication.LoginRequest;
import org.task.webapplication.dto.UserDto;
import org.task.webapplication.entity.User;
import org.task.webapplication.jwt.JwtUtil;
import org.task.webapplication.mail.EmailService;
import org.task.webapplication.mapper.UserMapper;
import org.task.webapplication.repository.UserRepository;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final UserRepository repository;

    private final EmailService emailService;

    private final JwtUtil jwtUtil;

    @Override
    public User registerUser(UserDto userDto) {
        User userToSave = userMapper.mapUserDtoToUser(userDto);

        String token = jwtUtil.generateToken(userToSave.getEmail());

        String confirmationToken = "http://587.com/confirm?token=" + token;
        emailService.sendEmailVerification(userToSave.getEmail(), "Confirm email address", "Please, follow the link to verify your email address: " +  confirmationToken);

        userToSave.setIsEmailVerified(true);

        return repository.save(userToSave);
    }

    @Override
    public String loginUser(LoginRequest loginRequest) {

        User user = repository.findUserByEmail(loginRequest.email());

        if (user == null || !user.getPassword().equals(loginRequest.password())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!user.getIsEmailVerified()) {
            throw new RuntimeException("Email not verified. Please verify your email first");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

    @Override
    public String resendEmailConfirmation(UserDto userDto) {
        return null;
    }

    @Override
    public String confirmEmail(UserDto userDto) {
        return null;
    }

    @Override
    public String changePasswordEmail(UserDto userDto) {
        return null;
    }

    @Override
    public String changePassword(String password) {
        return null;
    }

    @Override
    public String ping() {
        return "pong";
    }

    @Override
    public User getUserProfile(UUID uuid) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }
}
