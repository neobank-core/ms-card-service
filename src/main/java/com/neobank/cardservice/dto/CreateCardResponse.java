package com.neobank.cardservice.dto;

import java.util.UUID;

public record CreateCardResponse(
        UUID cardId,
        String status,
        String sessionToken
) {}