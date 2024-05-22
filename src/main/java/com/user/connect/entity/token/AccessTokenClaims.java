package com.user.connect.entity.token;

import com.user.connect.entity.user.Role;
import java.time.LocalDateTime;

public record AccessTokenClaims(
        LocalDateTime iat,
        LocalDateTime exp,
        String email,
        Role role
) {
}
