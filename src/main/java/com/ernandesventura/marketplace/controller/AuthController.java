package com.ernandesventura.marketplace.controller;

import com.ernandesventura.marketplace.dto.LoginRequest;
import com.ernandesventura.marketplace.dto.TokenResponse;
import com.ernandesventura.marketplace.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Login inválido não precisa de if/else aqui: o AuthService lança
    // CredenciaisInvalidasException e o GlobalExceptionHandler devolve 401
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ResponseEntity.ok(token);
    }
}
