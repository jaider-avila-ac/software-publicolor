package com.publicolor.finance.income.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class IngresoRequest {

    @NotBlank(message = "El concepto es obligatorio.")
    private String concept;

    @NotNull(message = "El valor es obligatorio.")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor a cero.")
    private BigDecimal amount;

    @NotNull(message = "La fecha es obligatoria.")
    private LocalDate incomeDate;

    private Long incomeCategoryId;

    private String notes;
}
