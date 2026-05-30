package com.neobank.cardservice.mapper;

import com.neobank.cardservice.dto.CardResponse;
import com.neobank.cardservice.entity.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getAccountId(),
                card.getCardNumberMasked(),
                card.getCardHolderName(),
                card.getExpiryMonth(),
                card.getExpiryYear(),
                card.getCardType(),
                card.getStatus(),
                card.getCreatedAt()
        );
    }
}
