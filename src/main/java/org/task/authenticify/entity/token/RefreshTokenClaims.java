package org.task.authenticify.entity.token;

import java.time.LocalDateTime;

public record RefreshTokenClaims(
        LocalDateTime iat,
        LocalDateTime exp,
        String email
) {
}
