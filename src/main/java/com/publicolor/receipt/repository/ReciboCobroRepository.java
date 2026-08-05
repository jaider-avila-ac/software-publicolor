package com.publicolor.receipt.repository;

import com.publicolor.receipt.model.ReciboCobro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReciboCobroRepository extends JpaRepository<ReciboCobro, Long> {

    @Query(value = "select nextval('receipt_consecutive_seq')", nativeQuery = true)
    Long siguienteConsecutivo();
}
