package com.nephro.prescriptionsservice.controller;

import com.nephro.prescriptionsservice.controller.dto.SignPrescriptionRequest;
import com.nephro.prescriptionsservice.dto.AiExplanationResponseDTO;
import com.nephro.prescriptionsservice.dto.ValidationResponseDTO;
import com.nephro.prescriptionsservice.entity.PrescribedMedication;
import com.nephro.prescriptionsservice.entity.Prescription;
import com.nephro.prescriptionsservice.service.PrescriptionAiService;
import com.nephro.prescriptionsservice.service.PrescriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {

    private final PrescriptionService service;
    private final PrescriptionAiService prescriptionAiService;

    public PrescriptionController(PrescriptionService service, PrescriptionAiService prescriptionAiService) {
        this.service = service;
        this.prescriptionAiService = prescriptionAiService;
    }

    @GetMapping
    public Page<Prescription> list(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.search(patientId, date, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Prescription getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Prescription create(@RequestBody Prescription prescription) {
        return service.create(prescription);
    }

    @PutMapping("/{id}")
    public Prescription update(@PathVariable Long id, @RequestBody Prescription prescription) {
        return service.update(id, prescription);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/{id}/sign")
    public Prescription sign(@PathVariable Long id, @RequestBody SignPrescriptionRequest req) {
        return service.sign(id, req.getSignatureData(), req.getSignedBy());
    }

    @GetMapping("/{prescriptionId}/medications")
    public List<PrescribedMedication> listMedications(@PathVariable Long prescriptionId) {
        return service.listMedications(prescriptionId);
    }

    @PostMapping("/{prescriptionId}/medications")
    public PrescribedMedication addMedication(
            @PathVariable Long prescriptionId,
            @RequestBody PrescribedMedication medication
    ) {
        return service.addMedication(prescriptionId, medication);
    }

    @PutMapping("/{prescriptionId}/medications/{medicationId}")
    public PrescribedMedication updateMedication(
            @PathVariable Long prescriptionId,
            @PathVariable Long medicationId,
            @RequestBody PrescribedMedication medication
    ) {
        return service.updateMedication(prescriptionId, medicationId, medication);
    }

    @DeleteMapping("/{prescriptionId}/medications/{medicationId}")
    public void deleteMedication(
            @PathVariable Long prescriptionId,
            @PathVariable Long medicationId
    ) {
        service.deleteMedication(prescriptionId, medicationId);
    }

    @GetMapping("/{id}/validation")
    public ValidationResponseDTO validatePrescription(@PathVariable Long id) {
        Prescription p = service.getById(id);
        return service.validateAgainstLabReports(p.getPatientId());
    }

    @GetMapping("/{id}/ai-explanation")
    public AiExplanationResponseDTO explainPrescription(@PathVariable Long id) {
        return prescriptionAiService.explainPrescriptionForElderly(id);
    }
}