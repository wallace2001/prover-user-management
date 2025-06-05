package com.prover.prover_test.application.service;

import com.prover.prover_test.domain.model.Client;
import com.prover.prover_test.domain.model.dto.ClientRequest;
import com.prover.prover_test.domain.repository.ClientRepository;
import com.prover.prover_test.infraestructure.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @InjectMocks
    private ClientService clientService;

    @Mock
    private ClientRepository repository;

    private Client client;

    @BeforeEach
    void setup() {
        client = Client.builder()
                .id(UUID.randomUUID())
                .name("Fulano")
                .email("fulano@mail.com")
                .phone("999999999")
                .csvFilePath("uploads/csv/file.csv")
                .build();
    }

    @Test
    void shouldSaveClient() {
        var request = new ClientRequest("Fulano", "fulano@mail.com", "999999999");

        when(repository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(client);

        var response = clientService.save(request);

        assertThat(response.name()).isEqualTo("Fulano");
        assertThat(response.email()).isEqualTo("fulano@mail.com");
        verify(repository).save(any(Client.class));
    }

    @Test
    void shouldThrowIfEmailAlreadyExists() {
        var request = new ClientRequest("Fulano", "fulano@mail.com", "999999999");
        when(repository.findByEmail(request.email())).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> clientService.save(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("E-mail já cadastrado.");
    }

    @Test
    void shouldDeleteClient() {
        var id = UUID.randomUUID();
        doNothing().when(repository).deleteById(id);

        clientService.delete(id);

        verify(repository).deleteById(id);
    }

    @Test
    void shouldThrowWhenClientNotFoundOnUpdate() {
        var id = UUID.randomUUID();
        var request = new ClientRequest("Novo", "novo@mail.com", "88888888");
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.update(id, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Client not found");
    }

    @Test
    void shouldUpdateClientSuccessfully() {
        var id = UUID.randomUUID();
        var request = new ClientRequest("Atualizado", "atualizado@mail.com", "77777777");

        when(repository.findById(id)).thenReturn(Optional.of(client));
        when(repository.save(any())).thenReturn(client);

        var response = clientService.update(id, request);

        assertThat(response.name()).isEqualTo("Atualizado");
        assertThat(response.email()).isEqualTo("atualizado@mail.com");
        verify(repository).save(any(Client.class));
    }

    @Test
    void shouldFindClientsByName() {
        when(repository.findByNameContainingIgnoreCase("Fulano"))
                .thenReturn(List.of(client));

        var result = clientService.findByName("Fulano");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).email()).isEqualTo("fulano@mail.com");
    }

    @Test
    void shouldFindAllClients() {
        when(repository.findAll()).thenReturn(List.of(client));

        var result = clientService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Fulano");
    }


    @Test
    void shouldImportClientsFromCsv() throws Exception {
        String content = "Nome1,nome1@email.com,999111111\nNome2,nome2@email.com,999222222";
        MultipartFile file = new MockMultipartFile("file", "clients.csv", "text/csv", content.getBytes(StandardCharsets.UTF_8));

        when(repository.findByEmail(any())).thenReturn(Optional.empty());
        when(repository.saveAll(any())).thenReturn(List.of());

        clientService.importClients(file);

        verify(repository, atLeastOnce()).saveAll(any());
    }

    @Test
    void shouldSkipClientsAlreadyExistingInCsv() throws Exception {
        String csv = "Nome1,nome1@email.com,123456\nNome2,nome2@email.com,654321";
        MultipartFile file = new MockMultipartFile("file", "clients.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        when(repository.findByEmail(anyString())).thenReturn(Optional.of(new Client()));

        clientService.importClients(file);

        ArgumentCaptor<List<Client>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());

        assertThat(captor.getValue()).isEmpty();
    }

    @Test
    void shouldCreateUploadDirectoryIfNotExists() throws Exception {
        Path dir = Paths.get("uploads/csv");
        if (Files.exists(dir)) {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }

        String content = "Nome1,nome1@email.com,123456";
        MultipartFile file = new MockMultipartFile("file", "clients.csv", "text/csv", content.getBytes(StandardCharsets.UTF_8));

        when(repository.findByEmail(any())).thenReturn(Optional.empty());
        when(repository.saveAll(any())).thenReturn(List.of());

        clientService.importClients(file);

        assertThat(Files.exists(dir)).isTrue();
    }


    @Test
    void shouldThrowRuntimeExceptionOnIOException() {
        MultipartFile file = mock(MultipartFile.class);

        try {
            when(file.getBytes()).thenThrow(new IOException("Erro simulado"));

            assertThatThrownBy(() -> clientService.importClients(file))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Erro ao importar CSV");
        } catch (IOException e) {
            // nunca entra aqui
        }
    }

    public void deleteDirectoryIfExists(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ignored) {}
                    });
        }
    }

}
