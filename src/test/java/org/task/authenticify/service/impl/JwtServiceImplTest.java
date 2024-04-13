package org.task.authenticify.service.impl;

import io.jsonwebtoken.JwtParser;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.task.authenticify.entity.token.adapter.AccessTokenHandlerAdapter;
import org.task.authenticify.entity.token.adapter.RefreshTokenHandlerAdapter;
import org.task.authenticify.entity.user.Role;
import org.task.authenticify.entity.user.User;
import org.task.authenticify.jwt.AccessTokenProperties;
import org.task.authenticify.jwt.JwtProperties;
import java.security.PrivateKey;

@ExtendWith(MockitoExtension.class)
public class JwtServiceImplTest {

    @InjectMocks
    JwtServiceImpl jwtService;

    @Mock
    private JwtProperties jwtProperties;

    private PrivateKey refreshTokenPrivateKey;
    private JwtParser refreshTokenParser;
    private RefreshTokenHandlerAdapter refreshTokenAdapter;

    private PrivateKey accessTokenPrivateKey;
    private JwtParser accessTokenParser;
    private AccessTokenHandlerAdapter accessTokenAdapter;

    private User sampleUser;


//    @Test
//    void issueAccessToken_shouldReturnIssuedToken() {
//        sampleUser = new User();
//        sampleUser.setEmail("samepleEmail@gmail.com");
//        sampleUser.setRole(Role.USER);
//
//        when(jwtProperties.accessToken()).thenReturn(new AccessTokenProperties());
//
//        String accessToken = jwtService.issueAccessToken(sampleUser);
//
//        assertNotNull(accessToken);
//        assertTrue(accessToken.length() > 0);
//    }
}
