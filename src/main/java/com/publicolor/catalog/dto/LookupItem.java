package com.publicolor.catalog.dto;

import lombok.Builder;
import lombok.Getter;

/** Forma común de todo ítem de catálogo (id + nombre) para poblar selects del frontend. */
@Getter @Builder
public class LookupItem {
    private Long id;
    private String name;
}
