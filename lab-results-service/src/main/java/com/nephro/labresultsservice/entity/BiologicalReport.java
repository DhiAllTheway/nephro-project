package com.nephro.labresultsservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "biological_report")
public class BiologicalReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;

    private LocalDate reportDate;

    private String analysisType;

    private String laboratoryName;

    @Column(length = 1000)
    private String comment;

    // ✅ FIX: fetch = EAGER so results are loaded before JSON serialization
    // cascade + orphanRemoval => delete report deletes results
    @OneToMany(
            mappedBy = "biologicalReport",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    @Builder.Default
    private List<BiologicalResult> results = new ArrayList<>();

    // ---------- helpers ----------
    public void addResult(BiologicalResult r) {
        results.add(r);
        r.setBiologicalReport(this);
    }

    public void removeResult(BiologicalResult r) {
        results.remove(r);
        r.setBiologicalReport(null);
    }
}