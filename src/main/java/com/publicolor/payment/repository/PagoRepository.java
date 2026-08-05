package com.publicolor.payment.repository;

import com.publicolor.payment.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByTrabajo_IdOrderByPaymentDateDesc(Long trabajoId);

    List<Pago> findByTrabajo_Cliente_IdOrderByPaymentDateDesc(Long clientId);

    @Query("select coalesce(sum(p.amount), 0) from Pago p where p.trabajo.id = :jobId")
    BigDecimal sumAmountByTrabajoId(@Param("jobId") Long jobId);

    @Query("select coalesce(sum(p.amount), 0) from Pago p where p.trabajo.cliente.id = :clientId")
    BigDecimal sumAmountByClientId(@Param("clientId") Long clientId);

    @Query("select coalesce(sum(p.amount), 0) from Pago p where p.paymentDate = :date")
    BigDecimal sumAmountEnFecha(@Param("date") LocalDate date);

    @Query("select coalesce(sum(p.amount), 0) from Pago p where p.paymentDate between :from and :to")
    BigDecimal sumAmountEntreFechas(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select coalesce(sum(p.amount), 0) from Pago p")
    BigDecimal sumAmountHistorico();
}
