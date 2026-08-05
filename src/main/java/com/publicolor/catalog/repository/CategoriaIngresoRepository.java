package com.publicolor.catalog.repository;

import com.publicolor.catalog.model.CategoriaIngreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaIngresoRepository extends JpaRepository<CategoriaIngreso, Long> {
    List<CategoriaIngreso> findByActiveTrueOrderByName();
}
