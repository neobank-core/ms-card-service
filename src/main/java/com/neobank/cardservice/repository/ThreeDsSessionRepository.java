package com.neobank.cardservice.repository;

import com.neobank.cardservice.entity.ThreeDsSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ThreeDsSessionRepository extends JpaRepository<ThreeDsSession, UUID> {
    Optional<ThreeDsSession> findBySessionTokenAndCardId(String sessionToken, UUID cardId);
}
