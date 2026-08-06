package com.publicolor.payment.dto;

import com.publicolor.catalog.dto.LookupItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Builder
public class PagoResponse {
    private Long id;
    private String code;
    private Long jobId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private LookupItem paymentMethod;
    private String notes;
    private String origin;
    private LocalDateTime createdAt;
    private boolean annulled;
    private LocalDateTime annulledAt;
    private String annulledReason;
}
