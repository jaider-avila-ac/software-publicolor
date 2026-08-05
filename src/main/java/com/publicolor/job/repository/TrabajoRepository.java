package com.publicolor.job.repository;

import com.publicolor.job.model.EstadoCuenta;
import com.publicolor.job.model.Trabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TrabajoRepository extends JpaRepository<Trabajo, Long>, JpaSpecificationExecutor<Trabajo> {

    List<Trabajo> findTop10ByOrderByCreatedAtDesc();

    List<Trabajo> findByCliente_IdOrderByJobDateDesc(Long clienteId);

    long countByStatus(EstadoCuenta status);

    @Query("select coalesce(sum(t.totalAmount), 0) from Trabajo t " +
           "where t.status <> com.publicolor.job.model.EstadoCuenta.CANCELADA and t.jobDate = :date")
    BigDecimal sumVendidoEnFecha(@Param("date") LocalDate date);

    @Query("select coalesce(sum(t.totalAmount), 0) from Trabajo t " +
           "where t.status <> com.publicolor.job.model.EstadoCuenta.CANCELADA and t.jobDate between :from and :to")
    BigDecimal sumVendidoEntreFechas(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select coalesce(sum(t.totalAmount), 0) from Trabajo t " +
           "where t.status <> com.publicolor.job.model.EstadoCuenta.CANCELADA")
    BigDecimal sumVendidoHistorico();

    @Query("select coalesce(sum(t.totalAmount), 0) from Trabajo t " +
           "where t.cliente.id = :clientId and t.status <> com.publicolor.job.model.EstadoCuenta.CANCELADA")
    BigDecimal sumVendidoPorCliente(@Param("clientId") Long clientId);

    boolean existsByCliente_Id(Long clienteId);

    @Query(value = "select nextval('job_consecutive_seq')", nativeQuery = true)
    Long siguienteConsecutivo();
}
