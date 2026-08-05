package com.publicolor.finance.income.repository;

import com.publicolor.finance.income.model.IngresoManual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface IngresoManualRepository extends JpaRepository<IngresoManual, Long>, JpaSpecificationExecutor<IngresoManual> {

    @Query("select coalesce(sum(i.amount), 0) from IngresoManual i where i.incomeDate = :date")
    BigDecimal sumAmountEnFecha(@Param("date") LocalDate date);

    @Query("select coalesce(sum(i.amount), 0) from IngresoManual i where i.incomeDate between :from and :to")
    BigDecimal sumAmountEntreFechas(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select coalesce(sum(i.amount), 0) from IngresoManual i")
    BigDecimal sumAmountHistorico();
}
