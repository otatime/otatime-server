package com.otatime_server.error.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus errorCode;

    public CustomException(final HttpStatus errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
