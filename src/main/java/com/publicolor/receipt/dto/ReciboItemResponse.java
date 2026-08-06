package com.publicolor.receipt.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Builder
public class ReciboItemResponse {
    private String productType;
    private String description;
    private List<String> finishes;
    private List<String> laminations;
    private String notes;
    private BigDecimal totalAmount;
}
