package com.neobank.cardservice.repository;

import com.neobank.cardservice.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    List<Card> findAllByUserId(String userId);
    Optional<Card> findByIdAndUserId(UUID id, String userId);

}
