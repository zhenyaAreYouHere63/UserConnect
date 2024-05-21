package com.user.connect.dto;

import jakarta.validation.constraints.NotBlank;
import com.user.connect.validation.ValidationErrorMessage;

public record ChangePasswordRequestDto(

        @NotBlank(message = ValidationErrorMessage.NOT_BLANK_MESSAGE)
        String token,

        @NotBlank(message = ValidationErrorMessage.NOT_BLANK_MESSAGE)
        String newPassword
) implements ValidationErrorMessage {
}
