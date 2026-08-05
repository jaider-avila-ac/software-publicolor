package com.publicolor.job.dto;

import com.publicolor.catalog.dto.LookupItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder
public class ConceptoTrabajoResponse {
    private Long id;
    private LookupItem productType;
    private String description;
    private BigDecimal quantity;
    private BigDecimal width;
    private BigDecimal height;
    private LookupItem finish;
    private LookupItem lamination;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String notes;
}
