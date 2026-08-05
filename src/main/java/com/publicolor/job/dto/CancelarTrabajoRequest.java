package com.publicolor.job.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CancelarTrabajoRequest {
    /** Debe venir en true si el trabajo ya tiene pagos registrados (confirmación explícita). */
    private boolean force;
}
