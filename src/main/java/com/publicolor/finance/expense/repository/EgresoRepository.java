package com.publicolor.finance.expense.repository;

import com.publicolor.finance.expense.model.Egreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface EgresoRepository extends JpaRepository<Egreso, Long>, JpaSpecificationExecutor<Egreso> {

    @Query("select coalesce(sum(e.amount), 0) from Egreso e where e.expenseDate = :date")
    BigDecimal sumAmountEnFecha(@Param("date") LocalDate date);

    @Query("select coalesce(sum(e.amount), 0) from Egreso e where e.expenseDate between :from and :to")
    BigDecimal sumAmountEntreFechas(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select coalesce(sum(e.amount), 0) from Egreso e")
    BigDecimal sumAmountHistorico();
}
