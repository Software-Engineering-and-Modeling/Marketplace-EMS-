package com.ernandesventura.marketplace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    // Lido do application.properties (que lê da variável de ambiente JWT_SEGREDO)
    @Value("${jwt.segredo}")
    private String segredo;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Transforma o texto do segredo em uma chave HMAC SHA-256.
    // A mesma chave assina (encoder) e confere (decoder) os tokens.
    private SecretKey chaveSecreta() {
        byte[] bytes = segredo.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    // Gera (assina) tokens — usado pelo AuthService no login
    @Bean
    public JwtEncoder jwtEncoder() {
        return NimbusJwtEncoder.withSecretKey(chaveSecreta())
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    // Lê e valida tokens recebidos no header Authorization: Bearer <token>
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(chaveSecreta())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AutenticacaoEntryPoint autenticacaoEntryPoint) throws Exception {
        http
                // API com token não usa cookie de sessão, então não precisa de proteção CSRF
                .csrf(csrf -> csrf.disable())
                // Não guarda sessão no servidor: cada requisição traz o próprio token
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(rotas -> rotas
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        // Vitrine: qualquer pessoa vê o catálogo; login só para comprar/vender
                        .requestMatchers(HttpMethod.GET, "/produtos", "/produtos/*").permitAll()
                        // Página de erro do Spring: sem isso, qualquer erro (400, 500...) vira 401
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                // 401 em JSON (ErroResponse) quando falta token em rota protegida
                .exceptionHandling(erros -> erros.authenticationEntryPoint(autenticacaoEntryPoint))
                // Confere o token JWT de cada requisição usando o JwtDecoder acima;
                // token inválido/expirado também responde pelo AutenticacaoEntryPoint
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(autenticacaoEntryPoint)
                        .jwt(Customizer.withDefaults()));

        return http.build();
    }
}
