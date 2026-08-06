package com.publicolor.client.service;

import com.publicolor.client.dto.ClienteDetalleResponse;
import com.publicolor.client.dto.ClienteRequest;
import com.publicolor.client.dto.ClienteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteService {
    Page<ClienteResponse> listar(String search, Pageable pageable);
    ClienteDetalleResponse obtenerDetalle(Long id);
    ClienteResponse crear(ClienteRequest req);
    ClienteResponse actualizar(Long id, ClienteRequest req);
    void eliminar(Long id);
}
