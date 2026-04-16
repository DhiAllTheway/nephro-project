package com.nephro.prescriptionsservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationResponseDTO {

    private String level;          // OK / WARN / ERROR
    private String message;        // UI message

    private Long patientId;

    // from lab service (if exists)
    private Long labReportId;
    private String reportDate;     // "YYYY-MM-DD"

    private Integer abnormalCount;
    private Double maxSeverity;
}