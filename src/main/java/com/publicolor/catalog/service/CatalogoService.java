package com.publicolor.catalog.service;

import com.publicolor.catalog.dto.CatalogoItemAdminResponse;
import com.publicolor.catalog.dto.CatalogoItemRequest;
import com.publicolor.catalog.dto.CatalogosResponse;

import java.util.List;

public interface CatalogoService {

    /** Solo activos — para poblar selects del resto de la app. */
    CatalogosResponse obtenerTodos();

    // -- Tipos de producto --------------------------------------------------
    List<CatalogoItemAdminResponse> listarTiposProducto();
    CatalogoItemAdminResponse crearTipoProducto(CatalogoItemRequest req);
    CatalogoItemAdminResponse actualizarTipoProducto(Long id, CatalogoItemRequest req);

    // -- Acabados -------------------------------------------------------------
    List<CatalogoItemAdminResponse> listarAcabados();
    CatalogoItemAdminResponse crearAcabado(CatalogoItemRequest req);
    CatalogoItemAdminResponse actualizarAcabado(Long id, CatalogoItemRequest req);

    // -- Laminados ------------------------------------------------------------
    List<CatalogoItemAdminResponse> listarLaminados();
    CatalogoItemAdminResponse crearLaminado(CatalogoItemRequest req);
    CatalogoItemAdminResponse actualizarLaminado(Long id, CatalogoItemRequest req);

    // -- Métodos de pago --------------------------------------------------------
    List<CatalogoItemAdminResponse> listarMetodosPago();
    CatalogoItemAdminResponse crearMetodoPago(CatalogoItemRequest req);
    CatalogoItemAdminResponse actualizarMetodoPago(Long id, CatalogoItemRequest req);

    // -- Categorías de ingreso ----------------------------------------------------
    List<CatalogoItemAdminResponse> listarCategoriasIngreso();
    CatalogoItemAdminResponse crearCategoriaIngreso(CatalogoItemRequest req);
    CatalogoItemAdminResponse actualizarCategoriaIngreso(Long id, CatalogoItemRequest req);

    // -- Categorías de egreso -----------------------------------------------------
    List<CatalogoItemAdminResponse> listarCategoriasEgreso();
    CatalogoItemAdminResponse crearCategoriaEgreso(CatalogoItemRequest req);
    CatalogoItemAdminResponse actualizarCategoriaEgreso(Long id, CatalogoItemRequest req);
}
