package com.user.connect.entity.token;

import io.jsonwebtoken.Header;

public record RefreshToken(
        Header header,
        RefreshTokenClaims tokenClaims
) {
}
