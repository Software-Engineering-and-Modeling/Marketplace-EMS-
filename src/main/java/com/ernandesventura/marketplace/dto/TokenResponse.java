package com.ernandesventura.marketplace.dto;

public record TokenResponse(String token, String tipo, Long expiraEmSegundos) {
}
