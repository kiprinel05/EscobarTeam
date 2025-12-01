package org.example.gateway.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(successHandler()))
                .oauth2Client(Customizer.withDefaults())
                .authorizeExchange(exchange -> exchange
                        // Event Service - POST, PUT, DELETE necesita ADMIN
                        .pathMatchers(HttpMethod.POST, "/api/events/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/events/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/events/**").hasRole("ADMIN")
                        // Event Service - GET este permis pentru toți utilizatorii autentificați
                        .pathMatchers(HttpMethod.GET, "/api/events/**").authenticated()
                        
                        // Stage Service - POST, PUT, DELETE necesita ADMIN
                        .pathMatchers(HttpMethod.POST, "/api/stages/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/stages/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/stages/**").hasRole("ADMIN")
                        // Stage Service - GET este permis pentru toti utilizatorii autentificati
                        .pathMatchers(HttpMethod.GET, "/api/stages/**").authenticated()
                        

                        .pathMatchers("/actuator/**").hasRole("ADMIN")
                        

                        .anyExchange().authenticated())
                .csrf(csrf -> csrf.disable()); // Cross-Site Request Forgery

        return http.build();
    }

    @Bean
    public ServerAuthenticationSuccessHandler successHandler() {
        return (webFilterExchange, authentication) -> {
            System.out.println("Authenticated authorities at success: " + authentication.getAuthorities());
            

            String redirectUrl = webFilterExchange.getExchange().getRequest().getQueryParams()
                    .getFirst("redirect_uri");
            
            if (redirectUrl == null || redirectUrl.isEmpty()) {
                redirectUrl = "/api/events";
            }

            webFilterExchange
                    .getExchange()
                    .getResponse()
                    .setStatusCode(org.springframework.http.HttpStatus.FOUND);

            webFilterExchange
                    .getExchange()
                    .getResponse()
                    .getHeaders().set("Location", redirectUrl);

            return webFilterExchange.getExchange().getResponse().setComplete();
        };
    }


    @Bean
    public ReactiveOAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        OidcReactiveOAuth2UserService delegate = new OidcReactiveOAuth2UserService();

        return new ReactiveOAuth2UserService<OidcUserRequest, OidcUser>() {
            @Override
            public Mono<OidcUser> loadUser(OidcUserRequest userRequest) {
                return delegate.loadUser(userRequest)
                        .map(oidcUser -> {
                            Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());

                            String email = oidcUser.getEmail();
                            if (email != null) {

                                if (email.endsWith("@gmail.com")) {
                                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                                }

                                if (email.endsWith("@festival-admin.ro")) {
                                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                                }
                            }

                            System.out.println("Mapped authorities for " + email + ": " + mappedAuthorities);

                            return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
                        });
            }
        };
    }
}

