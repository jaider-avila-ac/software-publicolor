package com.publicolor.payment.model;

import com.publicolor.catalog.model.MetodoPago;
import com.publicolor.job.model.Trabajo;
import com.publicolor.shared.util.TimeUtil;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Un abono o pago sobre un trabajo. Ledger inmutable: no se edita ni se borra. */
@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código único (ej. "AB-0001"), se genera solo y nunca se repite entre pagos. */
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Trabajo trabajo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private MetodoPago metodoPago;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * CASH: plata que efectivamente entró. CREDIT_APPLIED: saldo a favor de un
     * trabajo anterior que se reasignó automáticamente a este trabajo — no es
     * plata nueva, así que se excluye de los totales de caja/ingresos para no
     * contarla dos veces.
     */
    @Column(name = "origin", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrigenPago origin = OrigenPago.CASH;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** Anulación: el registro no se borra, queda marcado y deja de contar en todos los totales. */
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
