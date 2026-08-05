package com.publicolor.catalog.repository;

import com.publicolor.catalog.model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    List<MetodoPago> findByActiveTrueOrderByName();
}
