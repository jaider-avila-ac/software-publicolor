package com.publicolor.job.model;

import com.publicolor.client.model.Cliente;
import com.publicolor.shared.util.TimeUtil;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Trabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Cliente cliente;

    @Column(name = "consecutive_number", nullable = false, unique = true)
    private Long consecutiveNumber;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "total_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoCuenta status = EstadoCuenta.ABIERTA;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "job_date", nullable = false)
    private LocalDate jobDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "trabajo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ConceptoTrabajo> conceptos = new ArrayList<>();

    @PrePersist
    void prePersist() {
        LocalDateTime now = TimeUtil.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = TimeUtil.now();
    }
}
