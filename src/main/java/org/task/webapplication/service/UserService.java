package org.task.webapplication.service;

import org.task.webapplication.LoginRequest;
import org.task.webapplication.dto.UserDto;
import org.task.webapplication.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {

    User registerUser(UserDto userDto);

    String loginUser(LoginRequest loginRequest);

    String resendEmailConfirmation(UserDto userDto);

    String confirmEmail(UserDto userDto);

    String changePasswordEmail(UserDto userDto);

    String changePassword(String password);

    String ping();

    User getUserProfile(UUID uuid);

    List<User> getAllUsers();
}
