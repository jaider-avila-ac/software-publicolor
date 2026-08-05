package com.publicolor.catalog.repository;

import com.publicolor.catalog.model.TipoProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoProductoRepository extends JpaRepository<TipoProducto, Long> {
    List<TipoProducto> findByActiveTrueOrderByName();
}
