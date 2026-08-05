package com.publicolor.catalog.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class CatalogosResponse {
    private List<LookupItem> productTypes;
    private List<LookupItem> finishes;
    private List<LookupItem> laminations;
    private List<LookupItem> paymentMethods;
    private List<LookupItem> incomeCategories;
    private List<LookupItem> expenseCategories;
}
