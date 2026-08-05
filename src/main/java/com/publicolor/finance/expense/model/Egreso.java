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

    @PrePersist
    void prePersist() {
        createdAt = TimeUtil.now();
    }
}
