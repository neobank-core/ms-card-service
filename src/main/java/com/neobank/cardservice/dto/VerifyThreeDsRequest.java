package com.neobank.cardservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record VerifyThreeDsRequest(
        @NotBlank String sessionToken,
        @NotBlank String otp
) {}