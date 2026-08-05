package com.publicolor.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ClienteRequest {

    @NotBlank(message = "El nombre del cliente es obligatorio.")
    private String name;
}
