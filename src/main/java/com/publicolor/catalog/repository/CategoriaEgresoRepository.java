package com.publicolor.catalog.repository;

import com.publicolor.catalog.model.CategoriaEgreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaEgresoRepository extends JpaRepository<CategoriaEgreso, Long> {
    List<CategoriaEgreso> findByActiveTrueOrderByName();
}
