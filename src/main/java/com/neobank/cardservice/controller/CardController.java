package com.neobank.cardservice.controller;

import com.neobank.cardservice.dto.CardResponse;
import com.neobank.cardservice.dto.CreateCardRequest;
import com.neobank.cardservice.entity.Card;
import com.neobank.cardservice.mapper.CardMapper;
import com.neobank.cardservice.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final CardMapper cardMapper;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/my")
    public ResponseEntity<List<CardResponse>> getMyCards(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        List<CardResponse> response = cardService.getUserCards(userId)
                .stream()
                .map(cardMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CardResponse> createCard(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid CreateCardRequest request
    ) {
        String userId = jwt.getSubject();
        Card card = cardService.createCard(request, userId);
        return ResponseEntity.ok(cardMapper.toResponse(card));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCard(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        Card card = cardService.getUserCard(id, userId);
        return ResponseEntity.ok(cardMapper.toResponse(card));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/{id}/block")
    public ResponseEntity<CardResponse> blockCard(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        Card card = cardService.blockCard(id, userId);
        return ResponseEntity.ok(cardMapper.toResponse(card));
    }
}
