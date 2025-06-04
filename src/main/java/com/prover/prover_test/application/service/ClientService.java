package com.prover.prover_test.application.service;

import com.prover.prover_test.domain.model.Client;
import com.prover.prover_test.domain.model.dto.ClientRequest;
import com.prover.prover_test.domain.model.dto.ClientResponse;
import com.prover.prover_test.domain.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public List<ClientResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ClientResponse> findByName(String name) {
        return repository.findByNameContainingIgnoreCase(name).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ClientResponse save(ClientRequest request) {
        Client client = Client.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .build();

        return toResponse(repository.save(client));
    }

    public ClientResponse update(UUID id, ClientRequest request) {
        Client client = repository.findById(id).orElseThrow(() -> new RuntimeException("Client not found"));

        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());

        return toResponse(repository.save(client));
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getCsvFilePath()
        );
    }
}
