package com.nephro.labresultsservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiologicalResultDTO {
    private Long id;
    private String parameterName;
    private Double value;
    private String unit;
    private Double normalMinValue;
    private Double normalMaxValue;

    // computed business logic
    private BiologicalStatus status; // NORMAL / LOW / HIGH
    private Double severity;         // equation score
    private Boolean isAbnormal;      // status != NORMAL
}