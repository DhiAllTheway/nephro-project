package com.nephro.prescriptionsservice.service;

import com.nephro.prescriptionsservice.entity.Prescription;
import com.nephro.prescriptionsservice.entity.PrescriptionStatus;
import com.nephro.prescriptionsservice.repository.PrescribedMedicationRepository;
import com.nephro.prescriptionsservice.repository.PrescriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepo;

    @Mock
    private PrescribedMedicationRepository medicationRepo;

    @InjectMocks
    private PrescriptionService service;

    @Test
    void shouldCreatePrescriptionAsDraftAndClearSignatureFields() {
        Prescription prescription = Prescription.builder()
                .id(99L)
                .doctorName("Dr House")
                .patientId(200L)
                .prescriptionDate(LocalDate.of(2026, 4, 15))
                .status(PrescriptionStatus.SIGNED)
                .signatureData("fake-signature")
                .signedBy("Someone")
                .build();

        when(prescriptionRepo.save(any(Prescription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Prescription created = service.create(prescription);

        assertNull(created.getId());
        assertEquals(PrescriptionStatus.DRAFT, created.getStatus());
        assertNull(created.getSignatureData());
        assertNull(created.getSignedAt());
        assertNull(created.getSignedBy());
    }

    @Test
    void shouldSignPrescriptionSuccessfully() {
        Prescription draft = Prescription.builder()
                .id(1L)
                .doctorName("Dr Strange")
                .patientId(201L)
                .prescriptionDate(LocalDate.of(2026, 4, 15))
                .status(PrescriptionStatus.DRAFT)
                .build();

        when(prescriptionRepo.findById(1L)).thenReturn(Optional.of(draft));
        when(prescriptionRepo.save(any(Prescription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Prescription signed = service.sign(1L, "base64-signature", "Dr Strange");

        assertEquals(PrescriptionStatus.SIGNED, signed.getStatus());
        assertEquals("base64-signature", signed.getSignatureData());
        assertEquals("Dr Strange", signed.getSignedBy());
        assertNotNull(signed.getSignedAt());
    }

    @Test
    void shouldThrowWhenSigningWithoutSignature() {
        Prescription draft = Prescription.builder()
                .id(2L)
                .doctorName("Dr Who")
                .patientId(202L)
                .prescriptionDate(LocalDate.of(2026, 4, 15))
                .status(PrescriptionStatus.DRAFT)
                .build();

        when(prescriptionRepo.findById(2L)).thenReturn(Optional.of(draft));

        assertThrows(SignatureRequiredException.class, () ->
                service.sign(2L, "", "Dr Who")
        );
    }

    @Test
    void shouldThrowWhenSigningAlreadySignedPrescription() {
        Prescription signed = Prescription.builder()
                .id(3L)
                .doctorName("Dr Cox")
                .patientId(203L)
                .prescriptionDate(LocalDate.of(2026, 4, 15))
                .status(PrescriptionStatus.SIGNED)
                .build();

        when(prescriptionRepo.findById(3L)).thenReturn(Optional.of(signed));

        assertThrows(PrescriptionLockedException.class, () ->
                service.sign(3L, "base64-signature", "Dr Cox")
        );
    }
}