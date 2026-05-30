package com.neobank.cardservice.service;

import com.neobank.cardservice.dto.CreateCardRequest;
import com.neobank.cardservice.dto.CreateCardResponse;
import com.neobank.cardservice.entity.Card;
import com.neobank.cardservice.entity.ThreeDsSession;
import com.neobank.cardservice.enums.CardStatus;
import com.neobank.cardservice.event.CardBlockedEvent;
import com.neobank.cardservice.event.CardCreatedEvent;
import com.neobank.cardservice.exception.CardNotFoundException;
import com.neobank.cardservice.publisher.CardEventPublisher;
import com.neobank.cardservice.repository.CardRepository;
import com.neobank.cardservice.repository.ThreeDsSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardEventPublisher cardEventPublisher;
    private final ThreeDsSessionRepository threeDsSessionRepository;

    private static final boolean THREE_DS_ENABLED = true;

    @Transactional
    public CreateCardResponse createCard(CreateCardRequest request, String userId) {
        Card card = Card.builder()
                .accountId(request.accountId())
                .userId(userId)
                .cardNumberMasked(generateMaskedCardNumber())
                .cardHolderName(request.cardHolderName())
                .expiryMonth(12)
                .expiryYear(Year.now().getValue() + 4)
                .cardType(request.cardType())
                .status(THREE_DS_ENABLED
                        ? CardStatus.PENDING_VERIFICATION
                        : CardStatus.ACTIVE)
                .build();

        Card savedCard = cardRepository.save(card);

        if (!THREE_DS_ENABLED) {
            cardEventPublisher.publishCardCreated(new CardCreatedEvent(
                    savedCard.getId(), savedCard.getAccountId(),
                    savedCard.getUserId(), savedCard.getCardNumberMasked()
            ));
            return new CreateCardResponse(savedCard.getId(), savedCard.getStatus().name(), null);
        }

        String sessionToken = UUID.randomUUID().toString();
        ThreeDsSession session = ThreeDsSession.builder()
                .cardId(savedCard.getId())
                .sessionToken(sessionToken)
                .otpCode("123456")
                .verified(false)
                .build();

        threeDsSessionRepository.save(session);

        return new CreateCardResponse(
                savedCard.getId(),
                savedCard.getStatus().name(),
                sessionToken
        );
    }

    @Transactional
    public Card verifyThreeDs(UUID cardId, String sessionToken, String otp, String userId) {
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardId));

        ThreeDsSession session = threeDsSessionRepository
                .findBySessionTokenAndCardId(sessionToken, cardId)
                .orElseThrow(() -> new CardNotFoundException("Session not found"));

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("3DS session expired");
        }

        if (!session.getOtpCode().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        card.setStatus(CardStatus.ACTIVE);
        session.setVerified(true);

        cardRepository.save(card);
        threeDsSessionRepository.save(session);

        cardEventPublisher.publishCardCreated(new CardCreatedEvent(
                card.getId(), card.getAccountId(),
                card.getUserId(), card.getCardNumberMasked()
        ));

        return card;
    }

    public List<Card> getUserCards(String userId) {
        return cardRepository.findAllByUserId(userId);
    }

    public Card getUserCard(UUID cardId, String userId) {
        return cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardId));
    }

    public Card getCardById(UUID cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardId));
    }

    @Transactional
    public Card blockCard(UUID cardId, String userId) {
        Card card = getUserCard(cardId, userId);
        return blockCard(card);
    }

    @Transactional
    public Card blockCardById(UUID cardId) {
        Card card = getCardById(cardId);
        return blockCard(card);
    }

    private Card blockCard(Card card) {
        card.setStatus(CardStatus.BLOCKED);
        Card savedCard = cardRepository.save(card);
        cardEventPublisher.publishCardBlocked(new CardBlockedEvent(
                savedCard.getId(), savedCard.getAccountId(), savedCard.getUserId()
        ));
        return savedCard;
    }

    private String generateMaskedCardNumber() {
        int lastFour = 1000 + Math.abs(UUID.randomUUID().hashCode() % 9000);
        return "**** **** **** " + lastFour;
    }
}
