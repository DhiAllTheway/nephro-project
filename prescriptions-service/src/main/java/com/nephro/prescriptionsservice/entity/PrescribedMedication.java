package com.nephro.prescriptionsservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(name = "prescribed_medication")
public class PrescribedMedication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String medicationName;
    private String dosage;      // e.g. "500mg"
    private String frequency;   // e.g. "2 times/day"
    private Integer durationDays;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    @JsonBackReference
    private Prescription prescription;
}