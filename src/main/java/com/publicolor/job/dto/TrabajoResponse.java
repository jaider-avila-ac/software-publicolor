package com.publicolor.job.dto;

import com.publicolor.catalog.dto.LookupItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder
public class TrabajoResponse {
    private Long id;
    private Long consecutiveNumber;
    private String code;
    private LookupItem client;
    private String title;
    private BigDecimal totalAmount;
    private BigDecimal totalPaid;
    private BigDecimal pendingAmount;
    private BigDecimal creditApplied;
    private String status;
    private String notes;
    private LocalDate jobDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ConceptoTrabajoResponse> items;
}
