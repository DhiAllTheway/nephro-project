package com.nephro.prescriptionsservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(name = "prescription")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String doctorName;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private Long patientId;

    private LocalDate prescriptionDate;

    // =========================
    // SIGNATURE BUSINESS LOGIC
    // =========================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PrescriptionStatus status = PrescriptionStatus.DRAFT;

    // Store base64 PNG (demo-friendly). For MySQL, use LONGTEXT.
    @Column(columnDefinition = "LONGTEXT")
    private String signatureData;

    private LocalDateTime signedAt;

    private String signedBy;

    // ✅ IMPORTANT: prevent lazy-loading during JSON serialization
    @JsonIgnore
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PrescribedMedication> medications = new ArrayList<>();
}