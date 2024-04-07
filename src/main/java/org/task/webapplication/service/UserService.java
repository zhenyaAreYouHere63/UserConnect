package org.task.webapplication.service;

import org.task.webapplication.request.ChangePasswordRequest;
import org.task.webapplication.request.LoginRequest;
import org.task.webapplication.dto.UserDto;
import org.task.webapplication.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {

    String registerUser(UserDto userDto);

    String loginUser(LoginRequest loginRequest);

    void resendEmailConfirmation(String email);

    void confirmEmail(String token);

    void changePasswordEmail(String email);

    void changePassword(ChangePasswordRequest request);

    String ping();

    User getUserProfile();

    List<UserDto> getAllUsers();
}
