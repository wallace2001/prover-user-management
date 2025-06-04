package com.prover.prover_test.domain.model.dto;

import java.util.UUID;

public record ClientResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String csvFilePath
) {}
