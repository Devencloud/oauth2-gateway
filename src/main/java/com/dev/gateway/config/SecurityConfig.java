package com.dev.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import reactor.core.publisher.Mono;
import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ReactiveClientRegistrationRepository clientRegistrationRepository) {

        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/login/**", "/oauth2/**", "/login-success", "/logged-out").permitAll()
                        .anyExchange().authenticated())
                .oauth2Login(Customizer.withDefaults())
                .logout(logout -> logout
                        .requiresLogout(ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/logout"))
                        .logoutSuccessHandler((exchange, authentication) ->
                            exchange.getExchange().getSession()
                                .flatMap(session -> session.invalidate())
                                .then(Mono.fromRunnable(() -> {
                                    exchange.getExchange().getResponse()
                                        .setStatusCode(HttpStatus.FOUND);
                                    exchange.getExchange().getResponse().getHeaders()
                                        .setLocation(URI.create(
                                            "https://oauth2-gateway-production.up.railway.app/logged-out"));
                                }))
                        ))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                new RedirectServerAuthenticationEntryPoint(
                                        "/oauth2/authorization/auth-server")))
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}