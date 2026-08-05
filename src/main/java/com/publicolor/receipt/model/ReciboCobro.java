package com.publicolor.receipt.model;

import com.publicolor.job.model.Trabajo;
import com.publicolor.shared.util.TimeUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Registro de auditoría: cada vez que se pulsa "Generar recibo de cobro" se crea una fila. */
@Entity
@Table(name = "collection_receipts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReciboCobro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Trabajo trabajo;

    @Column(name = "consecutive_number", nullable = false, unique = true)
    private Long consecutiveNumber;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @PrePersist
    void prePersist() {
        generatedAt = TimeUtil.now();
    }
}
