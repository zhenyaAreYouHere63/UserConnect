package com.user.connect.service;

import com.user.connect.entity.token.TokenPair;
import com.user.connect.dto.ChangePasswordRequestDto;
import com.user.connect.dto.user.UserDto;
import java.util.List;

public interface UserService {

    TokenPair registerUser(UserDto userDto);

    void resendEmailConfirmation(String email);

    void confirmEmail(String token);

    void changePasswordEmail(String email);

    void changePassword(ChangePasswordRequestDto request);

    String ping();

    UserDto getUserProfile();

    List<UserDto> getAllUsers();
}
