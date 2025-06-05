package com.prover.prover_test.application.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prover.prover_test.application.service.AuthService;
import com.prover.prover_test.domain.model.dto.AuthResponse;
import com.prover.prover_test.domain.model.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {AuthController.class, AuthControllerTest.TestConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private AuthService authService;

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    when(authService.login(any(LoginRequest.class))).thenReturn(new AuthResponse("mocked-token"));
  }

  @Test
  void shouldReturnTokenWhenLoginSuccessful() throws Exception {
    LoginRequest request = new LoginRequest("admin@mail.com", "123456");

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("mocked-token"));
  }

  @Configuration
  static class TestConfig {
    @Bean
    public AuthService authService() {
      return Mockito.mock(AuthService.class);
    }
  }
}
