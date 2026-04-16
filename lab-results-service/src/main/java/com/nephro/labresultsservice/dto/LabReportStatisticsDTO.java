package com.nephro.labresultsservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabReportStatisticsDTO {

    // Top KPIs
    private long totalReports;
    private long normalReports;
    private long abnormalReports;
    private double abnormalRate;   // 0..100
    private double avgSeverity;    // average severity across abnormal results

    // Distributions
    // Example keys: NORMAL / LOW / HIGH
    private Map<String, Long> statusDistribution;

    // Trend (for simple chart)
    private List<DailyPoint> dailyAbnormalReports;

    // Risk ranking
    private List<PatientRisk> topRiskPatients;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyPoint {
        private LocalDate date;
        private long abnormalReports;
        private long totalReports;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientRisk {
        private Long patientId;
        private long totalReports;
        private long abnormalReports;
        private double maxSeverity;
        private double riskScore; // custom business score
    }
}