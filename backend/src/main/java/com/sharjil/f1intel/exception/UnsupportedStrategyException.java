package com.sharjil.f1intel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedStrategyException extends RuntimeException {
    public UnsupportedStrategyException(String message) { super(message); }
}
