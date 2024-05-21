package com.user.connect.dto.user;

import java.util.Objects;
import java.util.UUID;
import com.user.connect.entity.user.Role;
import com.user.connect.validation.ValidationErrorMessage;
import jakarta.validation.constraints.NotBlank;

public record UserDto(

        UUID uuid,

        Role role,

        @NotBlank(message = ValidationErrorMessage.NOT_BLANK_MESSAGE)
        String email,

        @NotBlank(message = ValidationErrorMessage.NOT_BLANK_MESSAGE)
        String password
) implements ValidationErrorMessage {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(uuid, userDto.uuid) && role == userDto.role && Objects.equals(email, userDto.email) && Objects.equals(password, userDto.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, role, email, password);
    }
}
