package com.neobank.cardservice.service;

import com.neobank.cardservice.dto.CreateCardRequest;
import com.neobank.cardservice.entity.Card;
import com.neobank.cardservice.enums.CardStatus;
import com.neobank.cardservice.event.CardBlockedEvent;
import com.neobank.cardservice.event.CardCreatedEvent;
import com.neobank.cardservice.exception.CardNotFoundException;
import com.neobank.cardservice.publisher.CardEventPublisher;
import com.neobank.cardservice.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardEventPublisher cardEventPublisher;

    @Transactional
    public Card createCard(CreateCardRequest request, String userId) {
        Card card = Card.builder()
                .accountId(request.accountId())
                .userId(userId)
                .cardNumberMasked(generateMaskedCardNumber())
                .cardHolderName(request.cardHolderName())
                .expiryMonth(12)
                .expiryYear(Year.now().getValue() + 4)
                .cardType(request.cardType())
                .status(CardStatus.ACTIVE)
                .build();

        Card savedCard = cardRepository.save(card);
        cardEventPublisher.publishCardCreated(new CardCreatedEvent(
                savedCard.getId(),
                savedCard.getAccountId(),
                savedCard.getUserId(),
                savedCard.getCardNumberMasked()
        ));
        return savedCard;
    }

    public List<Card> getUserCards(String userId) {
        return cardRepository.findAllByUserId(userId);
    }

    public Card getUserCard(UUID cardId, String userId) {
        return cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardId));
    }

    @Transactional
    public Card blockCard(UUID cardId, String userId) {
        Card card = getUserCard(cardId, userId);
        card.setStatus(CardStatus.BLOCKED);
        Card savedCard = cardRepository.save(card);
        cardEventPublisher.publishCardBlocked(new CardBlockedEvent(
                savedCard.getId(),
                savedCard.getAccountId(),
                savedCard.getUserId()
        ));
        return savedCard;
    }

    private String generateMaskedCardNumber() {
        int lastFour = 1000 + Math.abs(UUID.randomUUID().hashCode() % 9000);
        return "**** **** **** " + lastFour;
    }
}
