package com.publicolor.catalog.dto;

import lombok.Builder;
import lombok.Getter;

/** A diferencia de LookupItem, incluye "active" — se usa en la pantalla de administración de catálogos. */
@Getter @Builder
public class CatalogoItemAdminResponse {
    private Long id;
    private String name;
    private boolean active;
}
