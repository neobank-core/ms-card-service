package com.neobank.cardservice.client;

import com.neobank.cardservice.config.FeignInternalConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "account-service",
        url = "${account-service.url}",
        configuration = FeignInternalConfig.class
)
public interface AccountServiceClient {

    @GetMapping("/api/internal/accounts/{id}/owned-by/{userId}")
    boolean isAccountOwnedBy(@PathVariable UUID id, @PathVariable UUID userId);
}
