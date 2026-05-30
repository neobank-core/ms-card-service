package com.neobank.cardservice.dto;

import com.neobank.cardservice.enums.CardStatus;
import com.neobank.cardservice.enums.CardType;

import java.time.LocalDateTime;
import java.util.UUID;

public record CardResponse(UUID id,
                           UUID accountId,
                           String cardNumberMasked,
                           String cardHolderName,
                           Integer expiryMonth,
                           Integer expiryYear,
                           CardType cardType,
                           CardStatus cardStatus,
                           LocalDateTime createdAt) {
}