package com.publicolor.job.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Edición de los metadatos del trabajo — los conceptos se editan por endpoints separados. */
@Getter @Setter
public class TrabajoUpdateRequest {

    @NotNull(message = "El título del trabajo es obligatorio.")
    private String title;

    @NotNull(message = "La fecha del trabajo es obligatoria.")
    private LocalDate jobDate;

    @NotNull(message = "El valor total es obligatorio.")
    @DecimalMin(value = "0.00", message = "El valor total no puede ser negativo.")
    private BigDecimal totalAmount;

    private String notes;
}
