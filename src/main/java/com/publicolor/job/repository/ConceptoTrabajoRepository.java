package com.publicolor.job.repository;

import com.publicolor.job.model.ConceptoTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConceptoTrabajoRepository extends JpaRepository<ConceptoTrabajo, Long> {
    List<ConceptoTrabajo> findByTrabajo_IdOrderById(Long trabajoId);
}
