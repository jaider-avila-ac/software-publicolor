package com.publicolor.finance.expense.repository;

import com.publicolor.finance.expense.model.Egreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface EgresoRepository extends JpaRepository<Egreso, Long>, JpaSpecificationExecutor<Egreso> {

    List<Egreso> findByExpenseDateBetweenAndAnnulledFalseOrderByExpenseDateAsc(LocalDate from, LocalDate to);

    @Query("select coalesce(sum(e.amount), 0) from Egreso e where e.expenseDate = :date and e.annulled = false")
    BigDecimal sumAmountEnFecha(@Param("date") LocalDate date);

    @Query("select coalesce(sum(e.amount), 0) from Egreso e where e.expenseDate between :from and :to and e.annulled = false")
    BigDecimal sumAmountEntreFechas(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select coalesce(sum(e.amount), 0) from Egreso e where e.annulled = false")
    BigDecimal sumAmountHistorico();

    boolean existsByCode(String code);

    @Query(value = "select nextval('expense_consecutive_seq')", nativeQuery = true)
    Long siguienteConsecutivo();
}
