package com.nephro.labresultsservice.repository;

import com.nephro.labresultsservice.entity.BiologicalReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BiologicalReportRepository extends JpaRepository<BiologicalReport, Long> {

    Page<BiologicalReport> findByPatientId(Long patientId, Pageable pageable);

    Page<BiologicalReport> findByPatientIdAndReportDate(Long patientId, LocalDate reportDate, Pageable pageable);

    // ✅ latest report per patient (used by prescriptions-service validation)
    Optional<BiologicalReport> findTopByPatientIdOrderByReportDateDescIdDesc(Long patientId);

    // ✅ Statistics: range filter
    List<BiologicalReport> findByReportDateBetween(LocalDate from, LocalDate to);
}