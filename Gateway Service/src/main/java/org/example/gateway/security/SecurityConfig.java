package org.example.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/actuator/**", "/eureka/**").permitAll()

                        .pathMatchers(HttpMethod.OPTIONS).permitAll()

                        .pathMatchers(HttpMethod.POST, "/api/tickets/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/tickets/**").hasAnyRole("ADMIN", "USER")
                        .pathMatchers(HttpMethod.DELETE, "/api/tickets/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/artists/**").hasAnyRole("ADMIN", "USER")
                        .pathMatchers(HttpMethod.POST, "/api/artists/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/artists/**").hasRole("ADMIN")

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(new JwtRoleConverter())));

        return http.build();
    }
}
