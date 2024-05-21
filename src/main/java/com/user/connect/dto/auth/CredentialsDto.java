package com.user.connect.dto.auth;

import java.util.Objects;
import com.user.connect.validation.ValidationErrorMessage;
import jakarta.validation.constraints.NotBlank;

public record CredentialsDto(

        @NotBlank(message = ValidationErrorMessage.NOT_BLANK_MESSAGE)
        String email,

        @NotBlank(message = ValidationErrorMessage.NOT_BLANK_MESSAGE)
        String password) implements ValidationErrorMessage {


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CredentialsDto that = (CredentialsDto) o;
        return Objects.equals(email, that.email) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }
}
