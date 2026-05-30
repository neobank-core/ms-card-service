package com.neobank.cardservice.exception;

public class AccountAccessDeniedException extends RuntimeException {
    public AccountAccessDeniedException(String message) {
        super(message);
    }
}
