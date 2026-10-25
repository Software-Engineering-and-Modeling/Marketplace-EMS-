package com.ernandesventura.marketplace.exception;

// Usuário está logado, mas não pode mexer neste recurso (não é o dono) → 403
public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
