package com.user.connect.validation;

import lombok.Builder;

@Builder
public record ApiErrorDto(
        String code,
        String description) {

}
