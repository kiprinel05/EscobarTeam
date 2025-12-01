package org.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**", "/login/**", "/oauth2/**", "/id-token").permitAll()

                        .pathMatchers("GET", "/api/tickets/**").hasAnyRole("USER", "ADMIN", "TICKET_MANAGER")

                        .pathMatchers("POST", "/api/tickets/**").hasAnyRole("ADMIN", "TICKET_MANAGER")

                        .pathMatchers("PUT", "/api/tickets/**").hasAnyRole("ADMIN", "TICKET_MANAGER")

                        .pathMatchers("DELETE", "/api/tickets/**").hasRole("ADMIN")

                        .anyExchange().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/id-token")))
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public OidcReactiveOAuth2UserService oidcUserService() {
        OidcReactiveOAuth2UserService delegate = new OidcReactiveOAuth2UserService();

        return new OidcReactiveOAuth2UserService() {
            @Override
            public Mono<OidcUser> loadUser(OidcUserRequest userRequest) {
                return delegate.loadUser(userRequest)
                        .map(oidcUser -> {
                            String email = oidcUser.getEmail();

                            Set<SimpleGrantedAuthority> authorities = new HashSet<>();

                            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

                            if (email != null
                                    && (email.endsWith("@admin.com") || email.equals("paulgabryel12@gmail.com"))) {
                                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                            }

                            if (email != null
                                    && (email.endsWith("@manager.com") || email.equals("paul505824@gmail.com"))) {
                                authorities.add(new SimpleGrantedAuthority("ROLE_TICKET_MANAGER"));
                            }

                            return new DefaultOidcUser(
                                    authorities,
                                    oidcUser.getIdToken(),
                                    oidcUser.getUserInfo());
                        });
            }
        };
    }
}
