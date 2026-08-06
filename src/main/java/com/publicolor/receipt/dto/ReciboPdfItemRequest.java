package com.publicolor.receipt.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/** Mismos datos que ReciboItemResponse, pero deserializable: el frontend reenvía el recibo ya generado tal cual lo ve. */
@Getter @Setter
public class ReciboPdfItemRequest {
    private String productType;
    private String description;
    private List<String> finishes;
    private List<String> laminations;
    private String notes;
    private BigDecimal totalAmount;
}
