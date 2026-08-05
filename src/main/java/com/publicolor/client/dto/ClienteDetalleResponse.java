package com.publicolor.client.dto;

import com.publicolor.job.dto.TrabajoResponse;
import com.publicolor.payment.dto.PagoResponse;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder
public class ClienteDetalleResponse {
    private Long id;
    private String name;
    private BigDecimal totalPurchased;
    private BigDecimal totalPaid;
    private BigDecimal totalPending;
    private LocalDateTime createdAt;
    private List<TrabajoResponse> jobs;
    private List<PagoResponse> payments;
}
