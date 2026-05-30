package com.neobank.cardservice.controller;

import com.neobank.cardservice.dto.InternalCardResponse;
import com.neobank.cardservice.entity.Card;
import com.neobank.cardservice.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/internal/cards")
@RequiredArgsConstructor
public class InternalCardController {

    private final CardService cardService;

    @GetMapping("/{id}")
    public ResponseEntity<InternalCardResponse> getCard(@PathVariable UUID id) {
        Card card = cardService.getCardById(id);
        return ResponseEntity.ok(new InternalCardResponse(
                card.getId(),
                card.getAccountId(),
                card.getUserId(),
                card.getStatus(),
                card.getCardNumberMasked()
        ));
    }
}
