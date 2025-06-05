package com.prover.prover_test.application.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.prover.prover_test.domain.model.Role;
import com.prover.prover_test.domain.model.User;
import com.prover.prover_test.domain.model.dto.LoginRequest;
import com.prover.prover_test.domain.repository.UserRepository;
import com.prover.prover_test.infraestructure.security.JwtTokenProvider;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @InjectMocks private AuthService authService;

  @Mock private AuthenticationManager authenticationManager;

  @Mock private JwtTokenProvider jwtService;

  @Mock private UserRepository userRepository;

  @Test
  void shouldAuthenticateAndReturnToken() {
    String email = "admin@admin.com";
    String password = "admin123";

    Role role = Role.builder().id(UUID.randomUUID()).name("ADMIN").build();

    var user = User.builder().email(email).password(password).roles(Set.of(role)).build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(jwtService.generateToken(any())).thenReturn("token-jwt");
    when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));

    var result = authService.login(new LoginRequest(email, password));

    assertThat(result.token()).isEqualTo("token-jwt");
  }

  @Test
  void shouldThrowWhenUserNotFound() {
    when(userRepository.findByEmail("inexistente@admin.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(new LoginRequest("inexistente@admin.com", "123")))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining("User not found");
  }
}
