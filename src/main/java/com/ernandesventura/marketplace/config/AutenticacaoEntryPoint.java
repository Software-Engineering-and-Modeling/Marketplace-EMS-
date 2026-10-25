package com.ernandesventura.marketplace.config;

import com.ernandesventura.marketplace.dto.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.LocalDateTime;

// Chamado pelo Spring Security quando a requisição precisa de login e não tem
// um token válido. Erros desse tipo acontecem ANTES do controller, então o
// GlobalExceptionHandler não os vê: por isso esta classe monta o ErroResponse.
@Component
public class AutenticacaoEntryPoint implements AuthenticationEntryPoint {

    // Mapper de JSON do próprio Spring: mesmo formato de data dos outros erros
    private final JsonMapper jsonMapper;

    // Entry point padrão do Spring: põe o status 401 e o header
    // WWW-Authenticate: Bearer (padrão HTTP para dizer "mande um token")
    private final BearerTokenAuthenticationEntryPoint padrao = new BearerTokenAuthenticationEntryPoint();

    public AutenticacaoEntryPoint(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 1. Status 401 + header WWW-Authenticate
        padrao.commence(request, response, authException);

        // 2. Mensagem: token enviado mas inválido/expirado × nenhum token enviado
        String mensagem;
        if (authException instanceof InvalidBearerTokenException) {
            mensagem = "Token inválido ou expirado";
        } else {
            mensagem = "Autenticação necessária";
        }

        // 3. Corpo em JSON no mesmo formato dos outros erros
        ErroResponse erro = new ErroResponse(HttpStatus.UNAUTHORIZED.value(), mensagem, LocalDateTime.now());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonMapper.writeValueAsString(erro));
    }
}
