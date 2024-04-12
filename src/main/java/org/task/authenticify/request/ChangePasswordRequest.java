package org.task.authenticify.request;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(

        @NotBlank(message = "Field token cannot be a blank")
        String token,

        @NotBlank(message = "Field newPassword cannot be a blank")
        String newPassword
) {
}
