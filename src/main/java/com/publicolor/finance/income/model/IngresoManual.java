package com.publicolor.finance.income.model;

import com.publicolor.catalog.model.CategoriaIngreso;
import com.publicolor.shared.util.TimeUtil;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Ingreso registrado manualmente (no proviene de un pago de cuenta). */
@Entity
@Table(name = "manual_incomes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IngresoManual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_category_id")
    private CategoriaIngreso categoria;

    @Column(nullable = false, length = 255)
    private String concept;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "income_date", nullable = false)
    private LocalDate incomeDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "support_reference")
    private String supportReference;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = TimeUtil.now();
    }
}
