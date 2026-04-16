package com.nephro.labresultsservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiologicalReportDTO {
    private Long id;
    private Long patientId;
    private LocalDate reportDate;
    private String analysisType;
    private String laboratoryName;
    private String comment;

    private List<BiologicalResultDTO> results;

    // report-level summary (very demo-friendly)
    private Integer abnormalCount;
    private String reportStatus; // NORMAL / ABNORMAL
}