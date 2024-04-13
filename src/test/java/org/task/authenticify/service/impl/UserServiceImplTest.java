package org.task.authenticify.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.task.authenticify.dto.user.UserDto;
import org.task.authenticify.entity.token.AccessToken;
import org.task.authenticify.entity.token.AccessTokenClaims;
import org.task.authenticify.entity.user.Role;
import org.task.authenticify.entity.user.User;
import org.task.authenticify.exception.external.EmailNotVerifiedException;
import org.task.authenticify.exception.external.InvalidCredentialsException;
import org.task.authenticify.exception.external.UserNotFoundException;
import org.task.authenticify.mail.EmailService;
import org.task.authenticify.mapper.UserMapper;
import org.task.authenticify.repository.UserRepository;
import org.task.authenticify.request.ChangePasswordRequest;
import org.task.authenticify.service.JwtService;
import java.util.Optional;
import java.util.UUID;

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

//    @Test
//    void registerUser_shouldRegisterUserAndReturnTokenPair() {
//        String link = "www.google.com";
//
//        UUID uuid = UUID.randomUUID();
//        UserDto userDto = new UserDto(uuid, Role.USER, "email", "password");
//
//        User userToSave = new User();
//        userToSave.setUuid(uuid);
//        userToSave.setEmail(userDto.email());
//        String hashedPassword = BCrypt.hashpw(userDto.password(), BCrypt.gensalt());
//        userToSave.setPassword(hashedPassword);
//        userToSave.setIsEmailVerified(true);
//
//
//        User savedUser = new User();
//        savedUser.setUuid(uuid);
//        savedUser.setEmail(userDto.email());
//        savedUser.setPassword(userToSave.getPassword());
//        savedUser.setIsEmailVerified(userToSave.getIsEmailVerified());
//
//        when(userRepository.findUserByEmail(userDto.email())).thenReturn(Optional.empty());
//        when(userMapper.mapUserDtoToUser(userDto)).thenReturn(userToSave);
//        when(userRepository.save(userToSave)).thenReturn(savedUser);
//        when(jwtService.issueTokenPair(savedUser)).thenReturn(new TokenPair("accessToken", "refreshToken"));
//
//        TokenPair tokenPair = userService.registerUser(userDto);
//
//        verify(emailService).sendEmailVerification(eq(userToSave.getEmail()), eq("Confirm email address"),
//                eq("Please, follow the link to verify your email address: " + link));
//        verify(userRepository).save(userToSave);
//        assertNotNull(tokenPair);
//        assertEquals("accessToken", tokenPair.getAccessToken());
//        assertEquals("refreshToken", tokenPair.getRefreshToken());
//    }

    @Test
    void registerUser_shouldGetExceptionWhenEmailIsTaken() {
        UUID uuid = UUID.randomUUID();
        UserDto userDto = new UserDto(uuid, Role.USER, "email", "password");

        User existingUser = new User();
        existingUser.setEmail(userDto.email());
        existingUser.setPassword("passwordpassword");


        when(userRepository.findUserByEmail(userDto.email())).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.registerUser(userDto))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email [" + userDto.email() + "]. Given email is already taken");
    }

    @Test
    void resendEmailConfirmation_shouldResendEmail() {
        String link = "www.google.com";
        String email = "email";

        User user = new User();
        user.setEmail(email);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        userService.resendEmailConfirmation(email);

        assertTrue(user.getIsEmailVerified());
        verify(emailService, times(1)).sendEmailVerification(email, "Confirm email address",
                "Please, follow the link to verify your email address: " + link);
    }

    @Test
    void resendEmailConfirmation_ShouldReturnExceptionWhenUserEmailNotFound() {
        String email = "wrongEmail";

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
        String email = "wrongEmail";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePasswordEmail(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with [" + email +  "] not found");
    }

    @Test
    void changePassword_shouldChangePassword() {
        String token = "accessToken";
        String emailFromToken = "email";
        String password = "newPassword";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(token, password);

        User user = new User();
        user.setEmail(emailFromToken);
        user.setPassword("password");


        AccessToken accessToken = mock(AccessToken.class);
        AccessTokenClaims accessTokenClaims = mock(AccessTokenClaims.class);

        when(jwtService.parseAccessToken(token)).thenReturn(accessToken);
        when(accessToken.tokenClaims()).thenReturn(accessTokenClaims);
        when(accessTokenClaims.email()).thenReturn(emailFromToken);
        when(userRepository.findUserByEmail(emailFromToken)).thenReturn(Optional.of(user));

        userService.changePassword(changePasswordRequest);

        assertNotEquals(user.getPassword(), changePasswordRequest.newPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void changePassword_shouldGetExceptionWhenUserEmailNotFound() {
        String token = "accessToken";
        String emailFromToken = "email";
        String password = "newPassword";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(token, password);

        AccessToken accessToken = mock(AccessToken.class);
        AccessTokenClaims accessTokenClaims = mock(AccessTokenClaims.class);

        when(jwtService.parseAccessToken(token)).thenReturn(accessToken);
        when(accessToken.tokenClaims()).thenReturn(accessTokenClaims);
        when(accessTokenClaims.email()).thenReturn(emailFromToken);
        when(userRepository.findUserByEmail(emailFromToken)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(changePasswordRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with [" + emailFromToken +  "] not found");
    }

    @Test
    void ping_shouldReturnResponsePong() {
        String expectedResponse = "pong";

        String actualResponse = userService.ping();

        assertEquals(expectedResponse, actualResponse);
    }
}
