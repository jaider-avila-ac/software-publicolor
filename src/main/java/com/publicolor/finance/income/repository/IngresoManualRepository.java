package com.publicolor.finance.income.repository;

import com.publicolor.finance.income.model.IngresoManual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IngresoManualRepository extends JpaRepository<IngresoManual, Long>, JpaSpecificationExecutor<IngresoManual> {

    List<IngresoManual> findByIncomeDateBetweenAndAnnulledFalseOrderByIncomeDateAsc(LocalDate from, LocalDate to);

    @Query("select coalesce(sum(i.amount), 0) from IngresoManual i where i.incomeDate = :date and i.annulled = false")
    BigDecimal sumAmountEnFecha(@Param("date") LocalDate date);

    @Query("select coalesce(sum(i.amount), 0) from IngresoManual i where i.incomeDate between :from and :to and i.annulled = false")
    BigDecimal sumAmountEntreFechas(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select coalesce(sum(i.amount), 0) from IngresoManual i where i.annulled = false")
    BigDecimal sumAmountHistorico();

    boolean existsByCode(String code);

    @Query(value = "select nextval('income_consecutive_seq')", nativeQuery = true)
    Long siguienteConsecutivo();
}
