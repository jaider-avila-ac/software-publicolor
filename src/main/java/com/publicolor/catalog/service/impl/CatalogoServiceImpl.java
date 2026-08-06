package com.publicolor.catalog.service.impl;

import com.publicolor.catalog.dto.CatalogoItemAdminResponse;
import com.publicolor.catalog.dto.CatalogoItemRequest;
import com.publicolor.catalog.dto.CatalogosResponse;
import com.publicolor.catalog.dto.LookupItem;
import com.publicolor.catalog.model.*;
import com.publicolor.catalog.repository.*;
import com.publicolor.catalog.service.CatalogoService;
import com.publicolor.shared.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Transactional
public class CatalogoServiceImpl implements CatalogoService {

    private final TipoProductoRepository tipoProductoRepo;
    private final AcabadoRepository acabadoRepo;
    private final LaminadoRepository laminadoRepo;
    private final MetodoPagoRepository metodoPagoRepo;
    private final CategoriaIngresoRepository categoriaIngresoRepo;
    private final CategoriaEgresoRepository categoriaEgresoRepo;

    @Override
    @Transactional(readOnly = true)
    public CatalogosResponse obtenerTodos() {
        return CatalogosResponse.builder()
                .productTypes(mapLookup(tipoProductoRepo.findByActiveTrueOrderByName(), TipoProducto::getId, TipoProducto::getName))
                .finishes(mapLookup(acabadoRepo.findByActiveTrueOrderByName(), Acabado::getId, Acabado::getName))
                .laminations(mapLookup(laminadoRepo.findByActiveTrueOrderByName(), Laminado::getId, Laminado::getName))
                .paymentMethods(mapLookup(metodoPagoRepo.findByActiveTrueOrderByName(), MetodoPago::getId, MetodoPago::getName))
                .incomeCategories(mapLookup(categoriaIngresoRepo.findByActiveTrueOrderByName(), CategoriaIngreso::getId, CategoriaIngreso::getName))
                .expenseCategories(mapLookup(categoriaEgresoRepo.findByActiveTrueOrderByName(), CategoriaEgreso::getId, CategoriaEgreso::getName))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogoItemAdminResponse> listarTiposProducto() {
        return listarAdmin(tipoProductoRepo.findAll());
    }

    @Override
    public CatalogoItemAdminResponse crearTipoProducto(CatalogoItemRequest req) {
        return crear(tipoProductoRepo, TipoProducto::new, req);
    }

    @Override
    public CatalogoItemAdminResponse actualizarTipoProducto(Long id, CatalogoItemRequest req) {
        return actualizar(tipoProductoRepo, id, req);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogoItemAdminResponse> listarAcabados() {
        return listarAdmin(acabadoRepo.findAll());
    }

    @Override
    public CatalogoItemAdminResponse crearAcabado(CatalogoItemRequest req) {
        return crear(acabadoRepo, Acabado::new, req);
    }

    @Override
    public CatalogoItemAdminResponse actualizarAcabado(Long id, CatalogoItemRequest req) {
        return actualizar(acabadoRepo, id, req);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogoItemAdminResponse> listarLaminados() {
        return listarAdmin(laminadoRepo.findAll());
    }

    @Override
    public CatalogoItemAdminResponse crearLaminado(CatalogoItemRequest req) {
        return crear(laminadoRepo, Laminado::new, req);
    }

    @Override
    public CatalogoItemAdminResponse actualizarLaminado(Long id, CatalogoItemRequest req) {
        return actualizar(laminadoRepo, id, req);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogoItemAdminResponse> listarMetodosPago() {
        return listarAdmin(metodoPagoRepo.findAll());
    }

    @Override
    public CatalogoItemAdminResponse crearMetodoPago(CatalogoItemRequest req) {
        return crear(metodoPagoRepo, MetodoPago::new, req);
    }

    @Override
    public CatalogoItemAdminResponse actualizarMetodoPago(Long id, CatalogoItemRequest req) {
        return actualizar(metodoPagoRepo, id, req);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogoItemAdminResponse> listarCategoriasIngreso() {
        return listarAdmin(categoriaIngresoRepo.findAll());
    }

    @Override
    public CatalogoItemAdminResponse crearCategoriaIngreso(CatalogoItemRequest req) {
        return crear(categoriaIngresoRepo, CategoriaIngreso::new, req);
    }

    @Override
    public CatalogoItemAdminResponse actualizarCategoriaIngreso(Long id, CatalogoItemRequest req) {
        return actualizar(categoriaIngresoRepo, id, req);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogoItemAdminResponse> listarCategoriasEgreso() {
        return listarAdmin(categoriaEgresoRepo.findAll());
    }

    @Override
    public CatalogoItemAdminResponse crearCategoriaEgreso(CatalogoItemRequest req) {
        return crear(categoriaEgresoRepo, CategoriaEgreso::new, req);
    }

    @Override
    public CatalogoItemAdminResponse actualizarCategoriaEgreso(Long id, CatalogoItemRequest req) {
        return actualizar(categoriaEgresoRepo, id, req);
    }

    // -- Lógica genérica compartida por los 6 catálogos --------------------------------

    private <T extends CatalogoEntidad> CatalogoItemAdminResponse crear(
            JpaRepository<T, Long> repo, Supplier<T> factory, CatalogoItemRequest req) {
        T entidad = factory.get();
        entidad.setName(req.getName().trim());
        entidad.setActive(req.getActive() == null || req.getActive());
        return toAdminResponse(repo.save(entidad));
    }

    private <T extends CatalogoEntidad> CatalogoItemAdminResponse actualizar(
            JpaRepository<T, Long> repo, Long id, CatalogoItemRequest req) {
        T entidad = repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Elemento de catálogo no encontrado."));
        entidad.setName(req.getName().trim());
        if (req.getActive() != null) {
            entidad.setActive(req.getActive());
        }
        return toAdminResponse(repo.save(entidad));
    }

    private <T extends CatalogoEntidad> List<CatalogoItemAdminResponse> listarAdmin(List<T> entidades) {
        return entidades.stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(this::toAdminResponse)
                .toList();
    }

    private CatalogoItemAdminResponse toAdminResponse(CatalogoEntidad e) {
        return CatalogoItemAdminResponse.builder().id(e.getId()).name(e.getName()).active(e.isActive()).build();
    }

    private <T> List<LookupItem> mapLookup(List<T> items, Function<T, Long> idFn, Function<T, String> nameFn) {
        return items.stream()
                .map(item -> LookupItem.builder().id(idFn.apply(item)).name(nameFn.apply(item)).build())
                .toList();
    }
}
