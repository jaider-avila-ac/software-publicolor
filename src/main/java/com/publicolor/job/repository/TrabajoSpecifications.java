package com.publicolor.job.repository;

import com.publicolor.job.model.EstadoCuenta;
import com.publicolor.job.model.Trabajo;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/** Filtros combinables para el listado de trabajos (todos opcionales). */
public final class TrabajoSpecifications {

    private TrabajoSpecifications() {
    }

    public static Specification<Trabajo> conFiltros(Long clientId, EstadoCuenta status, LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (clientId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("cliente").get("id"), clientId));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (from != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("jobDate"), from));
            }
            if (to != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("jobDate"), to));
            }
            return predicate;
        };
    }
}
