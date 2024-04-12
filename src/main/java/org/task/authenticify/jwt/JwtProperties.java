package org.task.authenticify.jwt;

import org.springframework.stereotype.Component;

@Component
public record JwtProperties(
        AccessTokenProperties accessToken,
        RefreshTokenProperties refreshToken
) {

}
