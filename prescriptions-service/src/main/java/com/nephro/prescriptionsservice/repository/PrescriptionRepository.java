package com.nephro.prescriptionsservice.repository;

import com.nephro.prescriptionsservice.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Page<Prescription> findByPatientId(Long patientId, Pageable pageable);

    Page<Prescription> findByPrescriptionDate(LocalDate prescriptionDate, Pageable pageable);

    Page<Prescription> findByPatientIdAndPrescriptionDate(Long patientId, LocalDate prescriptionDate, Pageable pageable);
}