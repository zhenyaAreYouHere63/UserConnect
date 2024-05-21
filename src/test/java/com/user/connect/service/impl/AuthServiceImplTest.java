package com.user.connect.service.impl;

import java.util.Optional;
import com.user.connect.dto.auth.CredentialsDto;
import com.user.connect.entity.token.TokenPair;
import com.user.connect.entity.user.User;
import com.user.connect.exception.external.InvalidCredentialsException;
import com.user.connect.exception.external.UserNotFoundException;
import com.user.connect.repository.UserRepository;
import com.user.connect.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private CredentialsDto credentialsDto;

    private User user;

    @BeforeEach
    public void setUpData() {
        credentialsDto = new CredentialsDto("email@test.com", "testPassword");

        user = new User();
        user.setEmail(credentialsDto.email());
        user.setPassword(credentialsDto.password());
    }

    @Test
    void login_shouldLoginUserAndReturnTokenPair() {
        when(userRepository.findUserByEmail(credentialsDto.email()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(credentialsDto.password(), user.getPassword()))
                .thenReturn(true);
        when(jwtService.issueTokenPair(user)).thenReturn(new TokenPair("accessToken", "refreshToken"));

        TokenPair tokenPair = authServiceImpl.login(credentialsDto);

        assertNotNull(tokenPair);
        assertEquals("accessToken", tokenPair.getAccessToken());
        assertEquals("refreshToken", tokenPair.getRefreshToken());
        verify(jwtService, times(1)).issueTokenPair(user);
    }

    @Test
    void login_shouldReturnExceptionWhenUserEmailNotFound() {
        when(userRepository.findUserByEmail(credentialsDto.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authServiceImpl.login(credentialsDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with [" + credentialsDto.email() + "] not found");
    }

    @Test
    void login_shouldReturnExceptionWhenPasswordEncoderNotMatchesPasswords() {
        when(userRepository.findUserByEmail(credentialsDto.email()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(credentialsDto.password(), user.getPassword()))
                .thenReturn(false);

        assertThatThrownBy(() -> authServiceImpl.login(credentialsDto))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid password");
    }

    @Test
    void logout_shouldLogoutUser() {
        String email = "email@test.com";

        user = new User();
        user.setEmail(email);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        authServiceImpl.logout(email);

        verify(jwtService, times(1)).deleteAccessToken(email);
        verify(jwtService, times(1)).deleteRefreshToken(email);
    }

    @Test
    void logout_shouldReturnExceptionWhenUserEmailNotFound() {
        String email = "email@test.com";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authServiceImpl.logout(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with [" + email + "] not found");
    }
}
