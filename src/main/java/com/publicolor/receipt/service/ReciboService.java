package com.publicolor.receipt.service;

import com.publicolor.receipt.dto.ReciboResponse;

public interface ReciboService {
    /** Crea el registro de auditoría (consume un consecutivo nuevo) y devuelve el payload completo. */
    ReciboResponse generar(Long jobId);
}
