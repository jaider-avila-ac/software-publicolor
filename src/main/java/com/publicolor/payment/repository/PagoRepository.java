package com.publicolor.payment.repository;

import com.publicolor.payment.model.OrigenPago;
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

    /** Solo pagos reales (no crédito reasignado) sin anular, para mostrarlos como ingreso en los reportes. */
    List<Pago> findByPaymentDateBetweenAndOriginAndAnnulledFalseOrderByPaymentDateAsc(
            LocalDate from, LocalDate to, OrigenPago origin);

    /** Igual, pero incluye los anulados (para la lista de Ingresos en pantalla, donde se ven tachados). */
    List<Pago> findByPaymentDateBetweenAndOriginOrderByPaymentDateDesc(LocalDate from, LocalDate to, OrigenPago origin);

    boolean existsByCode(String code);

    @Query(value = "select nextval('payment_consecutive_seq')", nativeQuery = true)
    Long siguienteConsecutivo();

    /** Suma los pagos vigentes de un trabajo (efectivo + crédito aplicado, sin los anulados): determina si ese trabajo está saldado. */
    @Query("select coalesce(sum(p.amount), 0) from Pago p where p.trabajo.id = :jobId and p.annulled = false")
    BigDecimal sumAmountByTrabajoId(@Param("jobId") Long jobId);

    /** Cuánto de lo pagado en un trabajo vino de saldo a favor reasignado (para mostrarlo en detalle/recibo). */
    @Query("select coalesce(sum(p.amount), 0) from Pago p where p.trabajo.id = :jobId and p.origin = 'CREDIT_APPLIED' and p.annulled = false")
    BigDecimal sumCreditAppliedByTrabajoId(@Param("jobId") Long jobId);

    /** Solo plata real recibida de un cliente (excluye reasignaciones de crédito y pagos anulados). */
    @Query("select coalesce(sum(p.amount), 0) from Pago p where p.trabajo.cliente.id = :clientId and p.origin = 'CASH' and p.annulled = false")
    BigDecimal sumCashAmountByClientId(@Param("clientId") Long clientId);

    @Query("select coalesce(sum(p.amount), 0) from Pago p where p.paymentDate = :date and p.origin = 'CASH' and p.annulled = false")
    BigDecimal sumCashAmountEnFecha(@Param("date") LocalDate date);

    @Query("select coalesce(sum(p.amount), 0) from Pago p where p.paymentDate between :from and :to and p.origin = 'CASH' and p.annulled = false")
    BigDecimal sumCashAmountEntreFechas(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select coalesce(sum(p.amount), 0) from Pago p where p.origin = 'CASH' and p.annulled = false")
    BigDecimal sumCashAmountHistorico();
}
