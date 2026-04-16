package com.nephro.prescriptionsservice.dto;

import lombok.Data;

@Data
public class LabReportValidationDTO {

    private Integer abnormalCount;
    private String reportStatus;

}