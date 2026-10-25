package com.ernandesventura.marketplace.service;

import com.ernandesventura.marketplace.dto.LoginRequest;
import com.ernandesventura.marketplace.dto.TokenResponse;
import com.ernandesventura.marketplace.exception.CredenciaisInvalidasException;
import com.ernandesventura.marketplace.model.Usuario;
import com.ernandesventura.marketplace.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthService {

    // Mesma mensagem para email inexistente, usuário inativo e senha errada:
    // assim ninguém descobre quais emails estão cadastrados.
    private static final String MENSAGEM_ERRO = "Email ou senha inválidos";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiracao-segundos}")
    private Long expiracaoSegundos;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public TokenResponse login(LoginRequest request) {
        // 1. Busca o usuário (inativo não é encontrado, então não loga)
        Optional<Usuario> encontrado = usuarioRepository.findByEmailAndAtivoTrue(request.email());
        if (encontrado.isEmpty()) {
            throw new CredenciaisInvalidasException(MENSAGEM_ERRO);
        }
        Usuario usuario = encontrado.get();

        // 2. Compara a senha digitada com o hash BCrypt salvo no banco
        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException(MENSAGEM_ERRO);
        }

        // 3. Gera o token
        String token = gerarToken(usuario);
        return new TokenResponse(token, "Bearer", expiracaoSegundos);
    }

    private String gerarToken(Usuario usuario) {
        Instant agora = Instant.now();

        // Dados (claims) que vão dentro do token
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(usuario.getId().toString()) // quem é o dono do token: o id do usuário
                .issuedAt(agora)                     // quando foi gerado
                .expiresAt(agora.plusSeconds(expiracaoSegundos)) // quando deixa de valer
                .build();

        // Cabeçalho dizendo qual algoritmo assina o token (sem ele o encoder tentaria RS256)
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        JwtEncoderParameters parametros = JwtEncoderParameters.from(header, claims);
        return jwtEncoder.encode(parametros).getTokenValue();
    }
}
