package com.user.connect.validation;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalInputValidation {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ApiErrorDto> handleValidationException(Exception ex) {
        List<ApiErrorDto> errorInfos = new ArrayList<>();
        String title = "Validation exception";

        switch (ex) {
            case MethodArgumentNotValidException mnve -> {
                mnve.getBindingResult().getFieldErrors()
                        .forEach(fe -> errorInfos.add(new ApiErrorDto(title, String.format("[%s: '%s']",fe.getField(), fe.getDefaultMessage()))));
            }
            case ConstraintViolationException cve -> {
                cve.getConstraintViolations()
                        .forEach(v -> errorInfos.add(new ApiErrorDto(title, String.format("[%s: '%s']", v.getPropertyPath().toString(), v.getMessage()))));
            }
            default -> {
            }
        }
        return errorInfos;
    }
}
