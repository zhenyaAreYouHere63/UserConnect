package com.user.connect.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.user.connect.dto.ChangePasswordRequestDto;
import com.user.connect.dto.user.UserDto;
import com.user.connect.entity.token.AccessToken;
import com.user.connect.entity.token.AccessTokenClaims;
import com.user.connect.entity.user.Role;
import com.user.connect.entity.user.User;
import com.user.connect.exception.external.EmailNotVerifiedException;
import com.user.connect.exception.external.InvalidCredentialsException;
import com.user.connect.exception.external.UserNotFoundException;
import com.user.connect.mail.EmailService;
import com.user.connect.mapper.UserMapper;
import com.user.connect.repository.UserRepository;
import com.user.connect.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    private UserDto userDto;

    private User user;

    @BeforeEach
    public void setUpData() {
        UUID uuid = UUID.randomUUID();
        userDto = new UserDto(uuid, Role.USER, "email@test.com", "testPassword");

        user = new User();
        user.setUuid(uuid);
        user.setRole(userDto.role());
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());
    }

    @Test
    void registerUser_shouldGetExceptionWhenEmailIsTaken() {
        user.setPassword("passwordpassword");

        when(userRepository.findUserByEmail(userDto.email())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.registerUser(userDto))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email [" + userDto.email() + "]. Given email is already taken.");
    }

    @Test
    void resendEmailConfirmation_shouldResendEmail() {
        String link = "www.google.com";
        String email = "email@test.com";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        userService.resendEmailConfirmation(email);

        assertTrue(user.getIsEmailVerified());
        verify(emailService, times(1)).sendEmailVerification(email, "Confirm email address",
                "Please, follow the link to verify your email address: " + link);
    }

    @Test
    void resendEmailConfirmation_ShouldReturnExceptionWhenUserEmailNotFound() {
        String email = "wrongEmailTest@";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.resendEmailConfirmation(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with [" + email + "] not found");
    }

    @Test
    void confirmEmail_shouldConfirmEmail() {
        String email = "someEmail";
        String token = "accessToken";

        User user = new User();
        user.setEmail(email);

        AccessToken accessToken = mock(AccessToken.class);
        AccessTokenClaims accessTokenClaims = mock(AccessTokenClaims.class);

        when(jwtService.parseAccessToken(token)).thenReturn(accessToken);
        when(accessToken.tokenClaims()).thenReturn(accessTokenClaims);
        when(accessTokenClaims.email()).thenReturn(email);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.confirmEmail(token));
    }

    @Test
    void confirmEmail_shouldGetExceptionWhenUserEmailNotFound() {
        String token = "validToken";
        String email = "wrongEmail";

        AccessToken accessToken = mock(AccessToken.class);
        AccessTokenClaims accessTokenClaims = mock(AccessTokenClaims.class);

        when(jwtService.parseAccessToken(token)).thenReturn(accessToken);
        when(accessToken.tokenClaims()).thenReturn(accessTokenClaims);
        when(accessTokenClaims.email()).thenReturn(email);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.confirmEmail(token))
                .isInstanceOf(EmailNotVerifiedException.class)
                .hasMessage("Email not confirmed");
    }

    @Test
    void changePasswordEmail_shouldChangePasswordEmail() {
        String link = "www.google.com";

        User user = new User();
        String email = "email";
        user.setEmail(email);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        userService.changePasswordEmail(email);

        verify(emailService, times(1)).sendEmailVerification(email, "Change password email",
                "Please, follow the link to change your password: " + link);
    }

    @Test
    void changePasswordEmail_shouldGetExceptionWhenEmailNotFound() {
        String email = "wrongEmail@test.com";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePasswordEmail(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with [" + email +  "] not found");
    }

    @Test
    void changePassword_shouldChangePassword() {
        String token = "accessToken";
        String emailFromToken = "email@test.com";
        String password = "newPassword";
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDto(token, password);

        AccessToken accessToken = mock(AccessToken.class);
        AccessTokenClaims accessTokenClaims = mock(AccessTokenClaims.class);

        when(jwtService.parseAccessToken(token)).thenReturn(accessToken);
        when(accessToken.tokenClaims()).thenReturn(accessTokenClaims);
        when(accessTokenClaims.email()).thenReturn(emailFromToken);
        when(userRepository.findUserByEmail(emailFromToken)).thenReturn(Optional.of(user));

        userService.changePassword(changePasswordRequestDto);

        assertNotEquals(user.getPassword(), changePasswordRequestDto.newPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void changePassword_shouldGetExceptionWhenUserEmailNotFound() {
        String token = "accessToken";
        String emailFromToken = "email";
        String password = "newPassword";
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDto(token, password);

        AccessToken accessToken = mock(AccessToken.class);
        AccessTokenClaims accessTokenClaims = mock(AccessTokenClaims.class);

        when(jwtService.parseAccessToken(token)).thenReturn(accessToken);
        when(accessToken.tokenClaims()).thenReturn(accessTokenClaims);
        when(accessTokenClaims.email()).thenReturn(emailFromToken);
        when(userRepository.findUserByEmail(emailFromToken)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(changePasswordRequestDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with [" + emailFromToken +  "] not found");
    }

    @Test
    void ping_shouldReturnResponsePong() {
        String expectedResponse = "pong";

        String actualResponse = userService.ping();

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getUserProfile_shouldReturnCurrentLoggedInUser() {
        String token = "Bearer some_token";
        String accessToken = "accessToken";
        String email = "test@example.com";

        when(request.getHeader("Authorization")).thenReturn(token);

        AccessToken mockedAccessToken = mock(AccessToken.class);
        AccessTokenClaims accessTokenClaims = mock(AccessTokenClaims.class);

        when(jwtService.parseAccessToken(anyString())).thenReturn(mockedAccessToken);
        when(mockedAccessToken.tokenClaims()).thenReturn(accessTokenClaims);
        when(accessTokenClaims.email()).thenReturn(email);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        UserDto actualUserDto = userService.getUserProfile();

        assertEquals(userDto, actualUserDto);
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        UUID uuid2 = UUID.randomUUID();
        User user2 = new User();
        user2.setUuid(uuid2);
        user2.setEmail("email2");
        user2.setPassword("passwordpassword");

        List<User> expectedUsers = Arrays.asList(user, user2);

        when(userRepository.findAll()).thenReturn(expectedUsers);
        List<UserDto> actualUsers = userService.getAllUsers();

        assertThat(actualUsers).usingRecursiveComparison().isEqualTo(expectedUsers);
    }
}
