package com.neobank.cardservice.dto;

import com.neobank.cardservice.enums.CardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateCardRequest(
        @NotNull UUID accountId,
        @NotBlank String cardHolderName,
        @NotNull CardType cardType
) {
}
