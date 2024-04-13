package org.task.authenticify.dto.user;

import jakarta.validation.constraints.NotBlank;
import org.task.authenticify.entity.user.Role;
import java.util.Objects;
import java.util.UUID;

public record UserDto(

        UUID uuid,

        Role role,

        @NotBlank(message = "Field email cannot be blank")
        String email,

        @NotBlank(message = "Field password cannot be blank")
        String password
) {
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
