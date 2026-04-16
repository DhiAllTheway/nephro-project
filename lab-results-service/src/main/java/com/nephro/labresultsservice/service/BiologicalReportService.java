package com.nephro.labresultsservice.service;

import com.nephro.labresultsservice.dto.*;
import com.nephro.labresultsservice.entity.BiologicalReport;
import com.nephro.labresultsservice.entity.BiologicalResult;
import com.nephro.labresultsservice.repository.BiologicalReportRepository;
import com.nephro.labresultsservice.repository.BiologicalResultRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class BiologicalReportService {

    private final BiologicalReportRepository reportRepo;
    private final BiologicalResultRepository resultRepo;

    public BiologicalReportService(BiologicalReportRepository reportRepo, BiologicalResultRepository resultRepo) {
        this.reportRepo = reportRepo;
        this.resultRepo = resultRepo;
    }

    // -------------------------
    // REPORTS
    // -------------------------

    public Page<BiologicalReport> search(Long patientId, LocalDate date, Pageable pageable) {
        if (patientId != null && date != null) {
            return reportRepo.findByPatientIdAndReportDate(patientId, date, pageable);
        }
        if (patientId != null) {
            return reportRepo.findByPatientId(patientId, pageable);
        }
        return reportRepo.findAll(pageable);
    }

    public BiologicalReport getById(Long id) {
        return reportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found: " + id));
    }

    @Transactional(readOnly = true)
    public BiologicalReportDTO getDtoById(Long id) {
        return toDto(getById(id));
    }

    @Transactional(readOnly = true)
    public BiologicalReportDTO getLatestDtoByPatient(Long patientId) {
        BiologicalReport report = reportRepo.findTopByPatientIdOrderByReportDateDescIdDesc(patientId)
                .orElseThrow(() -> new RuntimeException("No report found"));
        return toDto(report);
    }

    @Transactional
    public BiologicalReport create(BiologicalReport report) {
        if (report.getResults() != null) {
            report.getResults().forEach(r -> r.setBiologicalReport(report));
        }
        return reportRepo.save(report);
    }

    @Transactional
    public BiologicalReport update(Long id, BiologicalReport updated) {
        BiologicalReport existing = getById(id);

        existing.setPatientId(updated.getPatientId());
        existing.setReportDate(updated.getReportDate());
        existing.setAnalysisType(updated.getAnalysisType());
        existing.setLaboratoryName(updated.getLaboratoryName());
        existing.setComment(updated.getComment());

        return reportRepo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        reportRepo.deleteById(id);
    }

    // -------------------------
    // RESULTS
    // -------------------------

    @Transactional(readOnly = true)
    public List<BiologicalResult> listResults(Long reportId) {
        return getById(reportId).getResults();
    }

    @Transactional(readOnly = true)
    public List<BiologicalResultDTO> listResultDtos(Long reportId) {
        return getById(reportId).getResults().stream().map(this::toResultDto).toList();
    }

    @Transactional
    public BiologicalResult addResult(Long reportId, BiologicalResult result) {
        result.setBiologicalReport(getById(reportId));
        return resultRepo.save(result);
    }

    @Transactional
    public BiologicalResult updateResult(Long reportId, Long resultId, BiologicalResult updated) {
        BiologicalResult existing = resultRepo.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Result not found"));

        existing.setParameterName(updated.getParameterName());
        existing.setValue(updated.getValue());
        existing.setUnit(updated.getUnit());
        existing.setNormalMinValue(updated.getNormalMinValue());
        existing.setNormalMaxValue(updated.getNormalMaxValue());

        return resultRepo.save(existing);
    }

    @Transactional
    public void deleteResult(Long reportId, Long resultId) {
        resultRepo.deleteById(resultId);
    }

    // -------------------------
    // 📅 CALENDAR
    // -------------------------

    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getCalendarEvents() {

        List<CalendarEventDTO> events = new ArrayList<>();

        for (BiologicalReport report : reportRepo.findAll()) {

            List<BiologicalResultDTO> results = report.getResults()
                    .stream()
                    .map(this::toResultDto)
                    .toList();

            boolean hasAbnormal = results.stream().anyMatch(r -> Boolean.TRUE.equals(r.getIsAbnormal()));

            double maxSeverity = results.stream()
                    .filter(r -> Boolean.TRUE.equals(r.getIsAbnormal()))
                    .map(r -> r.getSeverity() == null ? 0.0 : r.getSeverity())
                    .max(Double::compareTo)
                    .orElse(0.0);

            LocalDate baseDate = report.getReportDate();

            // 📄 REPORT EVENT
            events.add(new CalendarEventDTO(
                    report.getId(),
                    report.getPatientId(),
                    "REPORT",
                    baseDate,
                    "Report created",
                    0.0
            ));

            // FOLLOW-UP
            LocalDate followUpDate;
            String type;

            if (!hasAbnormal) {
                followUpDate = baseDate.plusDays(30);
                type = "NORMAL";
            } else if (maxSeverity < 1.5) {
                followUpDate = baseDate.plusDays(7);
                type = "FOLLOW_UP";
            } else {
                followUpDate = baseDate.plusDays(2);
                type = "URGENT";
            }

            events.add(new CalendarEventDTO(
                    report.getId(),
                    report.getPatientId(),
                    type,
                    followUpDate,
                    "Follow-up",
                    maxSeverity
            ));
        }

        return events.stream()
                .sorted(Comparator.comparing(CalendarEventDTO::getDate))
                .toList();
    }

    // -------------------------
    // 📊 STATISTICS
    // -------------------------

    @Transactional(readOnly = true)
    public LabReportStatisticsDTO getStatistics(LocalDate from, LocalDate to) {

        if (from == null || to == null) {
            to = LocalDate.now();
            from = to.minusDays(30);
        }

        List<BiologicalReport> reports = reportRepo.findByReportDateBetween(from, to);

        long total = reports.size();
        long abnormal = reports.stream()
                .map(this::toDto)
                .filter(r -> r.getAbnormalCount() > 0)
                .count();

        return LabReportStatisticsDTO.builder()
                .totalReports(total)
                .normalReports(total - abnormal)
                .abnormalReports(abnormal)
                .abnormalRate(total == 0 ? 0 : (abnormal * 100.0 / total))
                .avgSeverity(0.0)
                .statusDistribution(Map.of())
                .dailyAbnormalReports(List.of())
                .topRiskPatients(List.of())
                .build();
    }

    // -------------------------
    // MAPPERS
    // -------------------------

    private BiologicalReportDTO toDto(BiologicalReport report) {
        List<BiologicalResultDTO> results = report.getResults()
                .stream()
                .map(this::toResultDto)
                .toList();

        int abnormal = (int) results.stream()
                .filter(r -> Boolean.TRUE.equals(r.getIsAbnormal()))
                .count();

        return BiologicalReportDTO.builder()
                .id(report.getId())
                .patientId(report.getPatientId())
                .reportDate(report.getReportDate())
                .analysisType(report.getAnalysisType())
                .laboratoryName(report.getLaboratoryName())
                .comment(report.getComment())
                .results(results)
                .abnormalCount(abnormal)
                .reportStatus(abnormal == 0 ? "NORMAL" : "ABNORMAL")
                .build();
    }

    private BiologicalResultDTO toResultDto(BiologicalResult r) {
        Double v = r.getValue();
        Double min = r.getNormalMinValue();
        Double max = r.getNormalMaxValue();

        BiologicalStatus status = (v < min) ? BiologicalStatus.LOW :
                (v > max) ? BiologicalStatus.HIGH : BiologicalStatus.NORMAL;

        double severity = (min != null && max != null)
                ? Math.abs(v - (min + max) / 2) / ((max - min) / 2)
                : 0;

        return BiologicalResultDTO.builder()
                .id(r.getId())
                .parameterName(r.getParameterName())
                .value(v)
                .unit(r.getUnit())
                .normalMinValue(min)
                .normalMaxValue(max)
                .status(status)
                .severity(severity)
                .isAbnormal(status != BiologicalStatus.NORMAL)
                .build();
    }
}