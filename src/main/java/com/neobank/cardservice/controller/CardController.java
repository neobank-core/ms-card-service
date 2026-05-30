package com.neobank.cardservice.controller;

import com.neobank.cardservice.dto.CardResponse;
import com.neobank.cardservice.dto.CreateCardRequest;
import com.neobank.cardservice.dto.CreateCardResponse;
import com.neobank.cardservice.dto.VerifyThreeDsRequest;
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
import java.util.Map;
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
    public ResponseEntity<CreateCardResponse> createCard(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid CreateCardRequest request) {
        return ResponseEntity.ok(cardService.createCard(request, jwt.getSubject()));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/{id}/verify-3ds")
    public ResponseEntity<CardResponse> verifyThreeDs(
            @PathVariable UUID id,
            @RequestBody @Valid VerifyThreeDsRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Card card = cardService.verifyThreeDs(id, request.sessionToken(), request.otp(), jwt.getSubject());
        return ResponseEntity.ok(cardMapper.toResponse(card));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCard(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        Card card = hasRole(jwt, "ADMIN")
                ? cardService.getCardById(id)
                : cardService.getUserCard(id, jwt.getSubject());
        return ResponseEntity.ok(cardMapper.toResponse(card));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/{id}/block")
    public ResponseEntity<CardResponse> blockCard(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        Card card = hasRole(jwt, "ADMIN")
                ? cardService.blockCardById(id)
                : cardService.blockCard(id, jwt.getSubject());
        return ResponseEntity.ok(cardMapper.toResponse(card));
    }

    private boolean hasRole(Jwt jwt, String role) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            return false;
        }
        Object roles = realmAccess.get("roles");
        return roles instanceof List<?> roleList && roleList.contains(role);
    }
}
