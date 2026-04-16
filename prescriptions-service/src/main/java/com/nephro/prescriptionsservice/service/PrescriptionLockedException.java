package com.nephro.prescriptionsservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PrescriptionLockedException extends RuntimeException {
    public PrescriptionLockedException(String message) {
        super(message);
    }
}