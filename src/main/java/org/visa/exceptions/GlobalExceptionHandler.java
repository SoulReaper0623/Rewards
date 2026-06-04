package org.visa.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.visa.Dtos.ErrorResponse;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEmail(DuplicateEmailException ex) {
        return error(409, "DUPLICATE_EMAIL", ex.getMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMemberNotFound(MemberNotFoundException ex) {
        return error(404, "MEMBER_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(PointTypeInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePointTypeInvalid(PointTypeInvalidException ex) {
        return error(400, "INVALID_POINT_TYPE", ex.getMessage());
    }

    @ExceptionHandler(BalanceNotSufficientException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleBalanceNotSufficient(BalanceNotSufficientException ex) {
        return error(422, "INSUFFICIENT_BALANCE", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return error(400, "VALIDATION_ERROR", message);
    }

    private ErrorResponse error(int status, String code, String message) {
        return new ErrorResponse(status, code, message, Instant.now());
    }
}
