package com.publicolor.catalog.service.impl;

import com.publicolor.catalog.dto.CatalogosResponse;
import com.publicolor.catalog.dto.LookupItem;
import com.publicolor.catalog.model.*;
import com.publicolor.catalog.repository.*;
import com.publicolor.catalog.service.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CatalogoServiceImpl implements CatalogoService {

    private final TipoProductoRepository tipoProductoRepo;
    private final AcabadoRepository acabadoRepo;
    private final LaminadoRepository laminadoRepo;
    private final MetodoPagoRepository metodoPagoRepo;
    private final CategoriaIngresoRepository categoriaIngresoRepo;
    private final CategoriaEgresoRepository categoriaEgresoRepo;

    @Override
    public CatalogosResponse obtenerTodos() {
        return CatalogosResponse.builder()
                .productTypes(map(tipoProductoRepo.findByActiveTrueOrderByName(), TipoProducto::getId, TipoProducto::getName))
                .finishes(map(acabadoRepo.findByActiveTrueOrderByName(), Acabado::getId, Acabado::getName))
                .laminations(map(laminadoRepo.findByActiveTrueOrderByName(), Laminado::getId, Laminado::getName))
                .paymentMethods(map(metodoPagoRepo.findByActiveTrueOrderByName(), MetodoPago::getId, MetodoPago::getName))
                .incomeCategories(map(categoriaIngresoRepo.findByActiveTrueOrderByName(), CategoriaIngreso::getId, CategoriaIngreso::getName))
                .expenseCategories(map(categoriaEgresoRepo.findByActiveTrueOrderByName(), CategoriaEgreso::getId, CategoriaEgreso::getName))
                .build();
    }

    private <T> List<LookupItem> map(List<T> items, Function<T, Long> idFn, Function<T, String> nameFn) {
        return items.stream()
                .map(item -> LookupItem.builder().id(idFn.apply(item)).name(nameFn.apply(item)).build())
                .toList();
    }
}
