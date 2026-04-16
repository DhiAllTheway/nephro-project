package com.nephro.labresultsservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "biological_result")
public class BiologicalResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String parameterName;

    private Double value;

    private String unit;

    private Double normalMinValue;

    private Double normalMaxValue;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "biological_report_id", nullable = false)
    @JsonBackReference
    private BiologicalReport biologicalReport;
}