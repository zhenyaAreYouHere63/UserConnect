package org.task.authenticify.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.task.authenticify.dto.auth.CredentialsDto;
import org.task.authenticify.entity.token.TokenPair;
import org.task.authenticify.entity.user.User;
import org.task.authenticify.exception.external.InvalidCredentialsException;
import org.task.authenticify.exception.external.UserNotFoundException;
import org.task.authenticify.repository.UserRepository;
import org.task.authenticify.service.JwtService;
import java.util.Optional;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

//    @Mock
//    private Logger logger;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        when(logger.isInfoEnabled()).thenReturn(true);
//        doNothing().when(logger).info(any());
//    }

    @Test
    void login_shouldLoginUserAndReturnTokenPair() {
        String email = "email";
        String password = "password";
        CredentialsDto credentialsDto = new CredentialsDto(email, password);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        when(userRepository.findUserByEmail(credentialsDto.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(credentialsDto.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(jwtService.issueTokenPair(user)).thenReturn(new TokenPair("accessToken", "refreshToken"));

        TokenPair tokenPair = authServiceImpl.login(credentialsDto);

        assertNotNull(tokenPair);
        assertEquals("accessToken", tokenPair.getAccessToken());
        assertEquals("refreshToken", tokenPair.getRefreshToken());
        verify(jwtService, times(1)).issueTokenPair(user);
//        verify(logger, times(1)).info("User with email: [" + credentialsDto.getEmail() + "] logged in");
    }

    @Test
    void login_shouldReturnExceptionWhenUserEmailNotFound() {
        String email = "email";
        String password = "password";
        CredentialsDto credentialsDto = new CredentialsDto(email, password);

        when(userRepository.findUserByEmail(credentialsDto.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authServiceImpl.login(credentialsDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with [" + credentialsDto.getEmail() + "] not found");
    }

    @Test
    void login_shouldReturnExceptionWhenPasswordEncoderNotMatchesPasswords() {
        String email = "email";
        String password = "password";
        CredentialsDto credentialsDto = new CredentialsDto(email, password);

        User user = new User();
        user.setEmail(email);
        user.setPassword("currentPassword");

        when(userRepository.findUserByEmail(credentialsDto.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(credentialsDto.getPassword(), user.getPassword()))
                .thenReturn(false);

        assertThatThrownBy(() -> authServiceImpl.login(credentialsDto))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid password");
    }

    @Test
    void logout_shouldLogoutUser() {
        String email = "email";

        User user = new User();
        user.setEmail(email);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        authServiceImpl.logout(email);

        verify(jwtService, times(1)).deleteAccessToken(email);
        verify(jwtService, times(1)).deleteRefreshToken(email);
    }

    @Test
    void logout_shouldReturnExceptionWhenUserEmailNotFound() {
        String email = "email";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authServiceImpl.logout(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with [" + email + "] not found");
    }
}
