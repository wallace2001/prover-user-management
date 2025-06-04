package com.prover.prover_test.domain.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(description = "E-mail do usuário", example = "admin@prover.com")
        String username,

        @Schema(description = "Senha do usuário", example = "admin123")
        String password
) {}
