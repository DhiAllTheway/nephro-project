package com.nephro.labresultsservice.dto;

import java.time.LocalDate;

public class CalendarEventDTO {

    private Long reportId;
    private Long patientId;
    private String type; // REPORT | NORMAL | FOLLOW_UP | URGENT
    private LocalDate date;
    private String label;
    private Double severity;

    public CalendarEventDTO() {}

    public CalendarEventDTO(Long reportId, Long patientId, String type, LocalDate date, String label, Double severity) {
        this.reportId = reportId;
        this.patientId = patientId;
        this.type = type;
        this.date = date;
        this.label = label;
        this.severity = severity;
    }

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Double getSeverity() { return severity; }
    public void setSeverity(Double severity) { this.severity = severity; }
}