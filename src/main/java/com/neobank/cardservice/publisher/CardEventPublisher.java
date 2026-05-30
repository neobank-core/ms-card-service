package com.neobank.cardservice.publisher;

import com.neobank.cardservice.event.CardBlockedEvent;
import com.neobank.cardservice.event.CardCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishCardCreated(CardCreatedEvent event) {
        kafkaTemplate.send("card.created", event.cardId().toString(), event);
    }

    public void publishCardBlocked(CardBlockedEvent event) {
        kafkaTemplate.send("card.blocked", event.cardId().toString(), event);
    }
}
