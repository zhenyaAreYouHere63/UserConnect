package org.task.authenticify.service;

import org.task.authenticify.dto.auth.TokenPair;
import org.task.authenticify.request.ChangePasswordRequest;
import org.task.authenticify.dto.user.UserDto;
import org.task.authenticify.entity.user.User;
import java.util.List;

public interface UserService {

    TokenPair registerUser(UserDto userDto);

    void resendEmailConfirmation(String email);

    void confirmEmail(String token);

    void changePasswordEmail(String email);

    void changePassword(ChangePasswordRequest request);

    String ping();

    User getUserProfile();

    List<UserDto> getAllUsers();
}
