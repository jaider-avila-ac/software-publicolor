package com.publicolor.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CatalogoItemRequest {

    @NotBlank(message = "El nombre es obligatorio.")
    private String name;

    /** Al crear, si no viene se asume activo. Al editar, permite desactivar sin borrar. */
    private Boolean active;
}
