package com.nephro.prescriptionsservice.service;

import com.nephro.prescriptionsservice.dto.ValidationResponseDTO;
import com.nephro.prescriptionsservice.entity.PrescribedMedication;
import com.nephro.prescriptionsservice.entity.Prescription;
import com.nephro.prescriptionsservice.entity.PrescriptionStatus;
import com.nephro.prescriptionsservice.repository.PrescribedMedicationRepository;
import com.nephro.prescriptionsservice.repository.PrescriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepo;
    private final PrescribedMedicationRepository medicationRepo;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gateway.url:http://localhost:8082}")
    private String gatewayUrl;

    public PrescriptionService(PrescriptionRepository prescriptionRepo,
                               PrescribedMedicationRepository medicationRepo) {
        this.prescriptionRepo = prescriptionRepo;
        this.medicationRepo = medicationRepo;
    }

    @Transactional(readOnly = true)
    public Page<Prescription> search(Long patientId, LocalDate date, Pageable pageable) {
        if (patientId != null && date != null) {
            return prescriptionRepo.findByPatientIdAndPrescriptionDate(patientId, date, pageable);
        }
        if (patientId != null) {
            return prescriptionRepo.findByPatientId(patientId, pageable);
        }
        if (date != null) {
            return prescriptionRepo.findByPrescriptionDate(date, pageable);
        }
        return prescriptionRepo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Prescription getById(Long id) {
        return prescriptionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found: " + id));
    }

    public Prescription create(Prescription prescription) {
        prescription.setId(null);

        if (prescription.getStatus() == null) {
            prescription.setStatus(PrescriptionStatus.DRAFT);
        }

        if (prescription.getStatus() != PrescriptionStatus.DRAFT) {
            prescription.setStatus(PrescriptionStatus.DRAFT);
        }

        prescription.setSignatureData(null);
        prescription.setSignedAt(null);
        prescription.setSignedBy(null);

        return prescriptionRepo.save(prescription);
    }

    public Prescription update(Long id, Prescription prescription) {
        Prescription existing = getById(id);
        assertNotSigned(existing);

        existing.setDoctorName(prescription.getDoctorName());
        existing.setNotes(prescription.getNotes());
        existing.setPatientId(prescription.getPatientId());
        existing.setPrescriptionDate(prescription.getPrescriptionDate());

        return prescriptionRepo.save(existing);
    }

    public void delete(Long id) {
        if (!prescriptionRepo.existsById(id)) {
            throw new EntityNotFoundException("Prescription not found: " + id);
        }
        prescriptionRepo.deleteById(id);
    }

    // -------------------------
    // SIGN
    // -------------------------

    public Prescription sign(Long prescriptionId, String signatureData, String signedBy) {
        Prescription p = getById(prescriptionId);

        if (p.getStatus() == PrescriptionStatus.SIGNED) {
            throw new PrescriptionLockedException("Prescription already signed and locked.");
        }

        if (signatureData == null || signatureData.trim().isEmpty()) {
            throw new SignatureRequiredException("Signature is required to sign the prescription.");
        }

        p.setSignatureData(signatureData);
        p.setSignedBy((signedBy == null || signedBy.trim().isEmpty()) ? p.getDoctorName() : signedBy);
        p.setSignedAt(LocalDateTime.now());
        p.setStatus(PrescriptionStatus.SIGNED);

        return prescriptionRepo.save(p);
    }

    // -------------------------
    // MEDICATIONS
    // -------------------------

    @Transactional(readOnly = true)
    public List<PrescribedMedication> listMedications(Long prescriptionId) {
        if (!prescriptionRepo.existsById(prescriptionId)) {
            throw new EntityNotFoundException("Prescription not found: " + prescriptionId);
        }
        return medicationRepo.findByPrescriptionId(prescriptionId);
    }

    public PrescribedMedication addMedication(Long prescriptionId, PrescribedMedication medication) {
        Prescription prescription = getById(prescriptionId);
        assertNotSigned(prescription);

        medication.setId(null);
        medication.setPrescription(prescription);

        return medicationRepo.save(medication);
    }

    public PrescribedMedication updateMedication(Long prescriptionId, Long medicationId, PrescribedMedication medication) {
        Prescription prescription = getById(prescriptionId);
        assertNotSigned(prescription);

        PrescribedMedication existing = medicationRepo.findById(medicationId)
                .orElseThrow(() -> new EntityNotFoundException("Medication not found: " + medicationId));

        if (!existing.getPrescription().getId().equals(prescriptionId)) {
            throw new IllegalArgumentException("Medication does not belong to prescription " + prescriptionId);
        }

        existing.setMedicationName(medication.getMedicationName());
        existing.setDosage(medication.getDosage());
        existing.setFrequency(medication.getFrequency());
        existing.setDurationDays(medication.getDurationDays());

        return medicationRepo.save(existing);
    }

    public void deleteMedication(Long prescriptionId, Long medicationId) {
        Prescription prescription = getById(prescriptionId);
        assertNotSigned(prescription);

        PrescribedMedication existing = medicationRepo.findById(medicationId)
                .orElseThrow(() -> new EntityNotFoundException("Medication not found: " + medicationId));

        if (!existing.getPrescription().getId().equals(prescriptionId)) {
            throw new IllegalArgumentException("Medication does not belong to prescription " + prescriptionId);
        }

        medicationRepo.delete(existing);
    }

    private void assertNotSigned(Prescription p) {
        if (p.getStatus() == PrescriptionStatus.SIGNED) {
            throw new PrescriptionLockedException("Prescription is signed and cannot be modified.");
        }
    }

    // -------------------------
    // ✅ CROSS-SERVICE VALIDATION (REAL)
    // Calls: Gateway -> lab-results-service -> /lab-reports/latest/dto
    // -------------------------

    public ValidationResponseDTO validateAgainstLabReports(Long patientId) {
        try {
            String url = gatewayUrl + "/lab-results/lab-reports/latest/dto?patientId=" + patientId;

            // We read as Map to avoid creating shared DTO classes between services
            Map<?, ?> reportDto = restTemplate.getForObject(url, Map.class);

            if (reportDto == null) {
                return ValidationResponseDTO.builder()
                        .level("OK")
                        .message("No lab reports found for patient.")
                        .patientId(patientId)
                        .build();
            }

            Long reportId = reportDto.get("id") == null ? null : Long.valueOf(reportDto.get("id").toString());
            String reportDate = reportDto.get("reportDate") == null ? null : reportDto.get("reportDate").toString();

            Integer abnormalCount = reportDto.get("abnormalCount") == null ? 0 : Integer.valueOf(reportDto.get("abnormalCount").toString());

            Double maxSeverity = 0.0;
            Object resultsObj = reportDto.get("results");
            if (resultsObj instanceof List<?> list) {
                for (Object o : list) {
                    if (o instanceof Map<?, ?> r) {
                        Object sevObj = r.get("severity");
                        Object isAbnObj = r.get("isAbnormal");
                        boolean isAbn = isAbnObj != null && Boolean.parseBoolean(isAbnObj.toString());
                        if (!isAbn) continue;

                        double sev = sevObj == null ? 0.0 : Double.parseDouble(sevObj.toString());
                        if (sev > maxSeverity) maxSeverity = sev;
                    }
                }
            }

            String level;
            String msg;

            if (abnormalCount == 0) {
                level = "OK";
                msg = "Labs look NORMAL. Safe to prescribe.";
            } else if (maxSeverity >= 2.0) {
                level = "ERROR";
                msg = "High-risk lab abnormalities detected. Review required before prescribing.";
            } else {
                level = "WARN";
                msg = "Patient has abnormal lab results. Doctor should review before prescribing.";
            }

            return ValidationResponseDTO.builder()
                    .level(level)
                    .message(msg)
                    .patientId(patientId)
                    .labReportId(reportId)
                    .reportDate(reportDate)
                    .abnormalCount(abnormalCount)
                    .maxSeverity(Math.round(maxSeverity * 100.0) / 100.0)
                    .build();

        } catch (Exception e) {
            return ValidationResponseDTO.builder()
                    .level("WARN")
                    .message("Lab report service unavailable (Gateway/Eureka issue).")
                    .patientId(patientId)
                    .build();
        }
    }
}