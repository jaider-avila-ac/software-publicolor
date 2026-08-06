package com.publicolor.catalog.model;

/**
 * Forma común de los 6 catálogos (id, nombre, activo). Los Lombok @Getter/@Setter
 * de cada entidad ya generan estos métodos, así que implementarla no agrega código
 * — solo permite reusar una única lógica de administración (crear/editar/listar)
 * en vez de repetirla 6 veces.
 */
public interface CatalogoEntidad {
    Long getId();
    String getName();
    void setName(String name);
    boolean isActive();
    void setActive(boolean active);
}
