package com.publicolor.receipt.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder
public class ReciboItemResponse {
    private String productType;
    private String description;
    private BigDecimal totalAmount;
}
