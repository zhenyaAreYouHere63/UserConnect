package com.user.connect.entity.token;

import java.time.LocalDateTime;

public record RefreshTokenClaims(
        LocalDateTime iat,
        LocalDateTime exp,
        String email
) {
}
