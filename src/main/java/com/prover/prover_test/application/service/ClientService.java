package com.prover.prover_test.application.service;

import com.prover.prover_test.domain.model.Client;
import com.prover.prover_test.domain.model.dto.ClientRequest;
import com.prover.prover_test.domain.model.dto.ClientResponse;
import com.prover.prover_test.domain.repository.ClientRepository;
import com.prover.prover_test.infraestructure.exception.BusinessException;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ClientService {

  private final ClientRepository repository;

  public ClientService(ClientRepository repository) {
    this.repository = repository;
  }

  public void importClients(MultipartFile file) {
    try {
      Path storageDir = Paths.get("uploads/csv");
      if (!Files.exists(storageDir)) {
        Files.createDirectories(storageDir);
      }

      String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
      Path filePath = storageDir.resolve(fileName);

      Files.write(filePath, file.getBytes());

      try (BufferedReader reader = Files.newBufferedReader(filePath)) {
        List<Client> clients = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
          String[] parts = line.split(",");

          String name = parts[0];
          String email = parts[1];
          String phone = parts[2];

          if (repository.findByEmail(email).isEmpty()) {
            clients.add(
                Client.builder()
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .csvFilePath(filePath.toString())
                    .build());
          }
        }

        repository.saveAll(clients);
      }
    } catch (IOException e) {
      throw new RuntimeException("Erro ao importar CSV", e);
    }
  }

  public List<ClientResponse> findAll() {
    return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
  }

  public List<ClientResponse> findByName(String name) {
    return repository.findByNameContainingIgnoreCase(name).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  public ClientResponse save(ClientRequest request) {
    if (repository.findByEmail(request.email()).isPresent()) {
      throw new BusinessException("E-mail já cadastrado.");
    }

    Client client =
        Client.builder().name(request.name()).email(request.email()).phone(request.phone()).build();

    return toResponse(repository.save(client));
  }

  public ClientResponse update(UUID id, ClientRequest request) {
    Client client =
        repository.findById(id).orElseThrow(() -> new RuntimeException("Client not found"));

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
        client.getCsvFilePath());
  }
}
