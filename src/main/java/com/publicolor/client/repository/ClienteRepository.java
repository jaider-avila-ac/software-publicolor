package com.publicolor.client.repository;

import com.publicolor.client.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Page<Cliente> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
