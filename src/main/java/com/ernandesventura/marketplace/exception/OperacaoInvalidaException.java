package com.ernandesventura.marketplace.exception;

// Requisição viola uma regra de negócio (ex.: comprar o próprio produto) → 400
public class OperacaoInvalidaException extends RuntimeException {
    public OperacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
