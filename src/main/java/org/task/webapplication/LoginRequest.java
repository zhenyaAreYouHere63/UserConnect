package org.task.webapplication;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record LoginRequest(

        @NotBlank(message = "Field email cannot be blank")
        String email,

        @NotBlank(message = "Field email cannot be blank")
        String password
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginRequest that = (LoginRequest) o;
        return Objects.equals(email, that.email) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }
}
