package com.publicolor.catalog.repository;

import com.publicolor.catalog.model.Acabado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcabadoRepository extends JpaRepository<Acabado, Long> {
    List<Acabado> findByActiveTrueOrderByName();
}
