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

    /** Cliente existente. Si viene nulo, se usa clientName (se crea el cliente si no existe). */
    private Long clientId;

    /** Nombre del cliente cuando no se selecciona uno existente; se busca o se crea automáticamente. */
    private String clientName;

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
