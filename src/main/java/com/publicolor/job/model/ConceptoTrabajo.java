package com.publicolor.job.model;

import com.publicolor.catalog.model.Acabado;
import com.publicolor.catalog.model.Laminado;
import com.publicolor.catalog.model.TipoProducto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

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

    /** Un concepto puede tener varios acabados a la vez (ej. mate + transparente). */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "job_item_finishes",
            joinColumns = @JoinColumn(name = "job_item_id"),
            inverseJoinColumns = @JoinColumn(name = "finish_id"))
    @Builder.Default
    private Set<Acabado> acabados = new LinkedHashSet<>();

    /** Un concepto puede tener varios laminados a la vez. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "job_item_laminations",
            joinColumns = @JoinColumn(name = "job_item_id"),
            inverseJoinColumns = @JoinColumn(name = "lamination_id"))
    @Builder.Default
    private Set<Laminado> laminados = new LinkedHashSet<>();

    @Column(name = "unit_price", precision = 14, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
