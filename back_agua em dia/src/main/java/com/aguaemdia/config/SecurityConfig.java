package com.aguaemdia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de Segurança compatível com Spring Security 6 (Spring Boot 3.3+).
 * 
 * - Desativa CSRF via AbstractHttpConfigurer::disable para simplificar a integração com clientes REST.
 * - Libera todas as rotas HTTP (permitAll) para facilitar testes acadêmicos e integração do front estático.
 * - Desabilita a proteção de frames (X-Frame-Options) para que a interface web do H2 Console funcione perfeitamente.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desabilita CSRF usando a sintaxe recomendada do Spring Security 6
            .csrf(AbstractHttpConfigurer::disable)

            // 2. Libera todas as requisições HTTP para fins acadêmicos e testes
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )

            // 3. Desabilita frameOptions para permitir que o H2 Console seja renderizado em frames
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
            );

        return http.build();
    }

    /**
     * Bean para codificação e verificação de senhas com algoritmo BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
