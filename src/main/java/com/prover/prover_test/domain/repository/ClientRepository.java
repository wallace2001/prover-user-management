package com.prover.prover_test.domain.repository;

import com.prover.prover_test.domain.model.Client;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, UUID> {
  List<Client> findByNameContainingIgnoreCase(String name);

  Optional<Client> findByEmail(String email);
}
