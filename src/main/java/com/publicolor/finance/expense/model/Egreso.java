package com.publicolor.finance.expense.model;

import com.publicolor.catalog.model.CategoriaEgreso;
import com.publicolor.shared.util.TimeUtil;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Egreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código único (ej. "EG-0001"), se genera solo y nunca se repite entre egresos. */
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_category_id")
    private CategoriaEgreso categoria;

    @Column(nullable = false, length = 255)
    private String concept;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "support_reference")
    private String supportReference;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean annulled = false;

    @Column(name = "annulled_at")
    private LocalDateTime annulledAt;

    @Column(name = "annulled_reason", columnDefinition = "TEXT")
    private String annulledReason;

    @PrePersist
    void prePersist() {
        createdAt = TimeUtil.now();
    }
}
