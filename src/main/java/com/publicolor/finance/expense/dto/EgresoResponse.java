package com.publicolor.finance.expense.dto;

import com.publicolor.catalog.dto.LookupItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Builder
public class EgresoResponse {
    private Long id;
    private String code;
    private String concept;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private LookupItem category;
    private String notes;
    private LocalDateTime createdAt;
    private boolean annulled;
    private LocalDateTime annulledAt;
    private String annulledReason;
}
