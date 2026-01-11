package org.example.gateway.config;

import org.example.gateway.service.GoogleCloudIAMService;
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
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final GoogleCloudIAMService googleCloudIAMService;

    public SecurityConfig(GoogleCloudIAMService googleCloudIAMService) {
        this.googleCloudIAMService = googleCloudIAMService;
    }

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
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }

    @Bean
    public OidcReactiveOAuth2UserService oidcUserService() {
        OidcReactiveOAuth2UserService delegate = new OidcReactiveOAuth2UserService();

        return new OidcReactiveOAuth2UserService() {
            @Override
            public Mono<OidcUser> loadUser(OidcUserRequest userRequest) {
                return delegate.loadUser(userRequest)
                        .flatMap(oidcUser -> {
                            String email = oidcUser.getEmail();

                            return googleCloudIAMService.getUserRoles(email, userRequest.getAccessToken())
                                    .map(iamRoles -> {
                                        Set<String> appRoles = googleCloudIAMService
                                                .mapIamRolesToApplicationRoles(iamRoles);

                                        if (appRoles.size() <= 1) { // Only ROLE_USER
                                            appRoles.addAll(googleCloudIAMService.getDefaultRolesForEmail(email));
                                        }

                                        return appRoles;
                                    })
                                    .map(appRoles -> {
                                        Set<SimpleGrantedAuthority> authorities = appRoles.stream()
                                                .map(SimpleGrantedAuthority::new)
                                                .collect(Collectors.toSet());

                                        return (OidcUser) new DefaultOidcUser(
                                                authorities,
                                                oidcUser.getIdToken(),
                                                oidcUser.getUserInfo());
                                    });
                        });
            }
        };
    }
}
