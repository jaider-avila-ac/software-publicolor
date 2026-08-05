package com.publicolor.job.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class TrabajoRequest {

    @NotNull(message = "El cliente es obligatorio.")
    private Long clientId;

    @NotNull(message = "El título del trabajo es obligatorio.")
    private String title;

    @NotNull(message = "La fecha del trabajo es obligatoria.")
    private LocalDate jobDate;

    @NotNull(message = "El valor total es obligatorio.")
    @DecimalMin(value = "0.00", message = "El valor total no puede ser negativo.")
    private BigDecimal totalAmount;

    private String notes;

    @NotEmpty(message = "El trabajo debe tener al menos un concepto.")
    @Valid
    private List<ConceptoTrabajoRequest> items;
}
