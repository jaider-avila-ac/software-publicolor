package com.publicolor.client.service.impl;

import com.publicolor.client.dto.ClienteDetalleResponse;
import com.publicolor.client.dto.ClienteRequest;
import com.publicolor.client.dto.ClienteResponse;
import com.publicolor.client.model.Cliente;
import com.publicolor.client.repository.ClienteRepository;
import com.publicolor.client.service.ClienteService;
import com.publicolor.job.service.TrabajoService;
import com.publicolor.payment.repository.PagoRepository;
import com.publicolor.payment.service.PagoService;
import com.publicolor.shared.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private static final Locale ES = Locale.forLanguageTag("es-CO");

    private final ClienteRepository clienteRepo;
    private final com.publicolor.job.repository.TrabajoRepository trabajoRepo;
    private final PagoRepository pagoRepo;
    private final TrabajoService trabajoService;
    private final PagoService pagoService;

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> listar(String search, Pageable pageable) {
        Page<Cliente> page = (search == null || search.isBlank())
                ? clienteRepo.findAll(pageable)
                : clienteRepo.findByNameContainingIgnoreCase(search.trim(), pageable);
        return page.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDetalleResponse obtenerDetalle(Long id) {
        Cliente cliente = clienteRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado."));

        BigDecimal comprado = trabajoRepo.sumVendidoPorCliente(id);
        BigDecimal pagado = pagoRepo.sumAmountByClientId(id);

        return ClienteDetalleResponse.builder()
                .id(cliente.getId())
                .name(cliente.getName())
                .totalPurchased(comprado)
                .totalPaid(pagado)
                .totalPending(comprado.subtract(pagado))
                .createdAt(cliente.getCreatedAt())
                .jobs(trabajoService.listarPorCliente(id))
                .payments(pagoService.listarPorCliente(id))
                .build();
    }

    @Override
    public ClienteResponse crear(ClienteRequest req) {
        Cliente cliente = Cliente.builder().name(normalizar(req.getName())).build();
        return toResponse(clienteRepo.save(cliente));
    }

    @Override
    public ClienteResponse actualizar(Long id, ClienteRequest req) {
        Cliente cliente = clienteRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado."));
        cliente.setName(normalizar(req.getName()));
        return toResponse(clienteRepo.save(cliente));
    }

    /** El nombre del cliente siempre se guarda en mayúsculas, sin espacios sobrantes. */
    private String normalizar(String name) {
        return name.trim().toUpperCase(ES);
    }

    private ClienteResponse toResponse(Cliente c) {
        BigDecimal comprado = trabajoRepo.sumVendidoPorCliente(c.getId());
        BigDecimal pagado = pagoRepo.sumAmountByClientId(c.getId());
        return ClienteResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .totalPurchased(comprado)
                .totalPaid(pagado)
                .totalPending(comprado.subtract(pagado))
                .createdAt(c.getCreatedAt())
                .build();
    }
}
