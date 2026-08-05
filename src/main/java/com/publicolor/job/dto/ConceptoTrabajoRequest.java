package com.publicolor.job.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class ConceptoTrabajoRequest {

    @NotNull(message = "El tipo de producto es obligatorio.")
    private Long productTypeId;

    private String description;

    @DecimalMin(value = "0.00", message = "La cantidad no puede ser negativa.")
    private BigDecimal quantity;

    @DecimalMin(value = "0.00", message = "El ancho no puede ser negativo.")
    private BigDecimal width;

    @DecimalMin(value = "0.00", message = "El alto no puede ser negativo.")
    private BigDecimal height;

    private Long finishId;

    private Long laminationId;

    @DecimalMin(value = "0.00", message = "El precio unitario no puede ser negativo.")
    private BigDecimal unitPrice;

    @NotNull(message = "El valor del concepto es obligatorio.")
    @DecimalMin(value = "0.00", message = "El valor del concepto no puede ser negativo.")
    private BigDecimal totalAmount;

    private String notes;
}
