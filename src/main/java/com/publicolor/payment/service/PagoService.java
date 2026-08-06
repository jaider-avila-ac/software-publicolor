package com.publicolor.payment.service;

import com.publicolor.payment.dto.PagoRequest;
import com.publicolor.payment.dto.PagoResponse;

import java.util.List;

public interface PagoService {
    PagoResponse registrar(PagoRequest req);
    List<PagoResponse> listarPorTrabajo(Long jobId);
    List<PagoResponse> listarPorCliente(Long clientId);
    PagoResponse anular(Long id, String reason);
}
