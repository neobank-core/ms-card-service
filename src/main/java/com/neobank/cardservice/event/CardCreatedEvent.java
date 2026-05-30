package com.neobank.cardservice.event;

import java.util.UUID;

public record CardCreatedEvent(
        UUID cardId,
        UUID accountId,
        String userId,
        String cardNumberMasked
) {
}
