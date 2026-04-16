package com.nephro.labresultsservice.controller;

import com.nephro.labresultsservice.dto.CalendarEventDTO;
import com.nephro.labresultsservice.dto.*;
import com.nephro.labresultsservice.entity.BiologicalReport;
import com.nephro.labresultsservice.entity.BiologicalResult;
import com.nephro.labresultsservice.service.BiologicalReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/lab-reports")
public class BiologicalReportController {

    private final BiologicalReportService service;

    public BiologicalReportController(BiologicalReportService service) {
        this.service = service;
    }

    // -------------------------
    // REPORTS
    // -------------------------

    @GetMapping
    public Page<BiologicalReport> list(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.search(patientId, date, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public BiologicalReport getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/{id}/dto")
    public BiologicalReportDTO getDtoById(@PathVariable Long id) {
        return service.getDtoById(id);
    }

    @GetMapping("/latest/dto")
    public BiologicalReportDTO getLatestDto(@RequestParam Long patientId) {
        return service.getLatestDtoByPatient(patientId);
    }

    // -------------------------
    // 📅 CALENDAR ENDPOINT 🔥
    // -------------------------

    @GetMapping("/calendar")
    public List<CalendarEventDTO> getCalendarEvents() {
        return service.getCalendarEvents();
    }

    // -------------------------
    // STATISTICS
    // -------------------------

    @GetMapping("/statistics")
    public LabReportStatisticsDTO statistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return service.getStatistics(from, to);
    }

    // -------------------------
    // CRUD
    // -------------------------

    @PostMapping
    public BiologicalReport create(@RequestBody BiologicalReport report) {
        return service.create(report);
    }

    @PutMapping("/{id}")
    public BiologicalReport update(@PathVariable Long id, @RequestBody BiologicalReport report) {
        return service.update(id, report);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // -------------------------
    // RESULTS
    // -------------------------

    @GetMapping("/{reportId}/results")
    public List<BiologicalResult> listResults(@PathVariable Long reportId) {
        return service.listResults(reportId);
    }

    @GetMapping("/{reportId}/results/dto")
    public List<BiologicalResultDTO> listResultDtos(@PathVariable Long reportId) {
        return service.listResultDtos(reportId);
    }

    @PostMapping("/{reportId}/results")
    public BiologicalResult addResult(@PathVariable Long reportId, @RequestBody BiologicalResult result) {
        return service.addResult(reportId, result);
    }

    @PutMapping("/{reportId}/results/{resultId}")
    public BiologicalResult updateResult(
            @PathVariable Long reportId,
            @PathVariable Long resultId,
            @RequestBody BiologicalResult result
    ) {
        return service.updateResult(reportId, resultId, result);
    }

    @DeleteMapping("/{reportId}/results/{resultId}")
    public void deleteResult(@PathVariable Long reportId, @PathVariable Long resultId) {
        service.deleteResult(reportId, resultId);
    }
}