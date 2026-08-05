package com.publicolor.finance.expense.repository;

import com.publicolor.finance.expense.model.Egreso;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class EgresoSpecifications {

    private EgresoSpecifications() {
    }

    public static Specification<Egreso> conFiltros(Long categoryId, LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (categoryId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("categoria").get("id"), categoryId));
            }
            if (from != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("expenseDate"), from));
            }
            if (to != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("expenseDate"), to));
            }
            return predicate;
        };
    }
}
