package com.publicolor.job.model;

import com.publicolor.catalog.model.Acabado;
import com.publicolor.catalog.model.Laminado;
import com.publicolor.catalog.model.TipoProducto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** Un concepto/producto cobrado dentro de un trabajo (banner, vinilo, mug, etc.). */
@Entity
@Table(name = "job_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConceptoTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Trabajo trabajo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_type_id", nullable = false)
    private TipoProducto tipoProducto;

    @Column(length = 255)
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal width;

    @Column(precision = 10, scale = 2)
    private BigDecimal height;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finish_id")
    private Acabado acabado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lamination_id")
    private Laminado laminado;

    @Column(name = "unit_price", precision = 14, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
