package org.task.authenticify.service;

import org.task.authenticify.entity.token.TokenPair;
import org.task.authenticify.entity.token.AccessToken;
import org.task.authenticify.entity.token.RefreshToken;
import org.task.authenticify.entity.user.User;

public interface JwtService {

    String issueAccessToken(User user);

    String issueRefreshToken(User user);

    TokenPair issueTokenPair(User user);

    AccessToken parseAccessToken(String accessToken);

    RefreshToken parseRefreshToken(String refreshToken);

    boolean isTokenValid(String jwt, String email);

    boolean isTokenExpired(String jwt);

    void deleteAccessToken(String email);

    void deleteRefreshToken(String email);
}
