package com.prover.prover_test.application.service;

import com.prover.prover_test.domain.model.Role;
import com.prover.prover_test.domain.model.User;
import com.prover.prover_test.domain.model.dto.AuthResponse;
import com.prover.prover_test.domain.model.dto.LoginRequest;
import com.prover.prover_test.domain.repository.UserRepository;
import com.prover.prover_test.infraestructure.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authManager, JwtTokenProvider jwtProvider, UserRepository userRepo) {
        this.authenticationManager = authManager;
        this.jwtTokenProvider = jwtProvider;
        this.userRepository = userRepo;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = userRepository.findByEmail(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtTokenProvider.generateToken(auth);
        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        return new AuthResponse(token);
    }
}

