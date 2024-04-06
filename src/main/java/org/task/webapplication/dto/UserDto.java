package org.task.webapplication.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.UUID;

public record UserDto(
        UUID uuid,
        @NotBlank(message = "Field email cannot be blank")
        String email,
        @NotBlank(message = "Field password cannot be blank")
        String password
) {
        @Override
        public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null || getClass() != obj.getClass()) return false;
                UserDto userDto = (UserDto) obj;
                return Objects.equals(uuid, userDto.uuid);
        }

        @Override
        public int hashCode() {
                return Objects.hashCode(uuid);
        }
}
