package com.nephro.prescriptionsservice.controller.dto;

import lombok.Data;

@Data
public class SignPrescriptionRequest {
    private String signatureData; // base64 PNG (can include data:image/png;base64,...)
    private String signedBy;      // doctor name or user name
}