package com.publicolor.shared.dto;

import lombok.Getter;
import lombok.Setter;

/** Cuerpo común para anular un pago, ingreso o egreso. El motivo es opcional pero recomendado. */
@Getter @Setter
public class AnularRequest {
    private String reason;
}
