package com.prover.prover_test.domain.repository;

import com.prover.prover_test.domain.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    List<Client> findByNameContainingIgnoreCase(String name);

    Optional<Client> findByEmail(String email);
}