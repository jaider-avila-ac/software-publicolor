package com.publicolor.finance.income.repository;

import com.publicolor.finance.income.model.IngresoManual;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class IngresoManualSpecifications {

    private IngresoManualSpecifications() {
    }

    public static Specification<IngresoManual> conFiltros(Long categoryId, LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (categoryId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("categoria").get("id"), categoryId));
            }
            if (from != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("incomeDate"), from));
            }
            if (to != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("incomeDate"), to));
            }
            return predicate;
        };
    }
}
