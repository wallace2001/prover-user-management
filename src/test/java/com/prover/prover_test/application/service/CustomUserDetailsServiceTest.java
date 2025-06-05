package com.prover.prover_test.application.service;

import com.prover.prover_test.domain.model.Role;
import com.prover.prover_test.domain.model.User;
import com.prover.prover_test.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService service;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(UUID.randomUUID())
                .email("admin@mail.com")
                .password("123456")
                .roles(Set.of(Role.builder().name("ADMIN").build()))
                .build();
    }

    @Test
    void shouldLoadUserByUsername() {
        when(userRepository.findByEmail("admin@mail.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = service.loadUserByUsername("admin@mail.com");

        assertThat(userDetails.getUsername()).isEqualTo("admin@mail.com");
        assertThat(userDetails.getPassword()).isEqualTo("123456");
        assertThat(userDetails.getAuthorities()).extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("notfound@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("notfound@mail.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado");
    }
}
