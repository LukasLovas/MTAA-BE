package com.example.mtaa.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommonException extends RuntimeException {

    private final HttpStatus status;

    public CommonException(HttpStatus status, String message) {
        super(message, null, false, false);
        this.status = status;
    }
}
