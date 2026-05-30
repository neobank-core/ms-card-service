package com.neobank.cardservice.dto;

import com.neobank.cardservice.enums.CardStatus;

import java.util.UUID;

public record InternalCardResponse(
        UUID id,
        UUID accountId,
        String userId,
        CardStatus status,
        String cardNumberMasked
) {
}
