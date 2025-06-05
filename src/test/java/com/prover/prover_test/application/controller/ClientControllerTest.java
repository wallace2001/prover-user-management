package com.prover.prover_test.application.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prover.prover_test.application.service.ClientService;
import com.prover.prover_test.domain.model.dto.ClientRequest;
import com.prover.prover_test.domain.model.dto.ClientResponse;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ClientControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private final ClientService clientService = Mockito.mock(ClientService.class);

  @BeforeEach
  void setUp(@Autowired ClientController clientController) {
    org.springframework.test.util.ReflectionTestUtils.setField(
        clientController, "service", clientService);
  }

  @Test
  void shouldReturnAllClients() throws Exception {
    var client =
        new ClientResponse(UUID.randomUUID(), "Fulano", "fulano@mail.com", "999999999", null);
    Mockito.when(clientService.findAll()).thenReturn(List.of(client));

    mockMvc
        .perform(get("/api/clients"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Fulano"));
  }

  @Test
  void shouldSearchClientsByName() throws Exception {
    var client = new ClientResponse(UUID.randomUUID(), "Beltrano", "bel@x.com", "99999", null);
    Mockito.when(clientService.findByName("Bel")).thenReturn(List.of(client));

    mockMvc
        .perform(get("/api/clients/search").param("name", "Bel"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Beltrano"));
  }

  @Test
  void shouldCreateClient() throws Exception {
    var request = new ClientRequest("João", "joao@mail.com", "111111");
    var response = new ClientResponse(UUID.randomUUID(), "João", "joao@mail.com", "111111", null);

    Mockito.when(clientService.save(any())).thenReturn(response);

    mockMvc
        .perform(
            post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("joao@mail.com"));
  }

  @Test
  void shouldUpdateClient() throws Exception {
    var id = UUID.randomUUID();
    var request = new ClientRequest("Novo", "novo@mail.com", "000000");
    var response = new ClientResponse(id, "Novo", "novo@mail.com", "000000", null);

    Mockito.when(clientService.update(eq(id), any())).thenReturn(response);

    mockMvc
        .perform(
            put("/api/clients/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Novo"));
  }

  @Test
  void shouldDeleteClient() throws Exception {
    var id = UUID.randomUUID();

    mockMvc.perform(delete("/api/clients/" + id)).andExpect(status().isNoContent());
  }

  @Test
  void shouldImportClientsFromCsv() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile(
            "file", "clients.csv", "text/csv", "John,john@mail.com,123456\n".getBytes());

    mockMvc.perform(multipart("/api/clients/import").file(file)).andExpect(status().isOk());
  }
}
