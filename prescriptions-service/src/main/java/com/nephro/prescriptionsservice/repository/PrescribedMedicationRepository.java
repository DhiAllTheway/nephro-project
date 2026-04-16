package com.nephro.prescriptionsservice.repository;

import com.nephro.prescriptionsservice.entity.PrescribedMedication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescribedMedicationRepository extends JpaRepository<PrescribedMedication, Long> {

    // ✅ Used by GET /prescriptions/{id}/medications
    List<PrescribedMedication> findByPrescriptionId(Long prescriptionId);
}