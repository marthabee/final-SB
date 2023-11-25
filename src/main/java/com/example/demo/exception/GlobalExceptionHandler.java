package com.example.demo.exception;


import com.example.demo.message.GlobalMessage;
import com.example.demo.response.ErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Exception ứng dụng
    @ExceptionHandler(AppException.class)
    public ErrorResponse handlerAppException(AppException e) {
        log.trace(e.getMessage());
        return new ErrorResponse(e.getCode().value(), e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handlerNotFoundException(NotFoundException e) {
        log.trace(e.getMessage());
        return new ErrorResponse(e.getHttpStatus().value(), e.getMessage());
    }

    @ExceptionHandler(NoContentException.class)
    public ErrorResponse handlerNoContentException(NoContentException e) {
        log.trace(e.getMessage());
        return new ErrorResponse(e.getHttpStatus().value(), e.getMessage());
    }

    // Exception hệ thống
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerException(Exception e) {
        log.error(e.getMessage());
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), GlobalMessage.ERROR);
    }

}
