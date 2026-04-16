package com.nephro.prescriptionsservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SignatureRequiredException extends RuntimeException {
    public SignatureRequiredException(String message) {
        super(message);
    }
}