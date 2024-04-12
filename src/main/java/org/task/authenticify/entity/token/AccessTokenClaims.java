package org.task.authenticify.entity.token;

import org.task.authenticify.entity.user.Role;
import java.time.LocalDateTime;

public record AccessTokenClaims(
        LocalDateTime iat,
        LocalDateTime exp,
        String email,
        Role role
) {
}
