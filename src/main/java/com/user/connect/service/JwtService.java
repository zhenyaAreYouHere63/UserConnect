package com.user.connect.service;

import com.user.connect.entity.token.TokenPair;
import com.user.connect.entity.token.AccessToken;
import com.user.connect.entity.token.RefreshToken;
import com.user.connect.entity.user.User;

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
