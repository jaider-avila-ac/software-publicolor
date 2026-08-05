package com.publicolor.catalog.repository;

import com.publicolor.catalog.model.Laminado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LaminadoRepository extends JpaRepository<Laminado, Long> {
    List<Laminado> findByActiveTrueOrderByName();
}
