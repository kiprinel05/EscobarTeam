package org.example.gateway.config;

import org.example.gateway.service.GoogleCloudIAMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
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

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private GoogleCloudIAMService iamService;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .authorizeExchange(exchanges -> exchanges
                // Public endpoints
                .pathMatchers("/actuator/**", "/login/**", "/oauth2/**", 
                             "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", 
                             "/swagger-resources/**", "/webjars/**").permitAll()
                .pathMatchers("/id-token", "/role-mapping").authenticated()
                
                // Artist Service endpoints - GET permite acces fără autentificare pentru testare
                .pathMatchers("GET", "/api/artists/**").permitAll()
                .pathMatchers("POST", "/api/artists/**").hasAnyRole("ADMIN", "ARTIST_MANAGER")
                .pathMatchers("PUT", "/api/artists/**").hasAnyRole("ADMIN", "ARTIST_MANAGER")
                .pathMatchers("DELETE", "/api/artists/**").hasRole("ADMIN")
                
                // Event Service endpoints - GET permite acces fără autentificare pentru testare
                .pathMatchers("GET", "/api/events/**").permitAll()
                .pathMatchers("POST", "/api/events/**").hasAnyRole("ADMIN", "ARTIST_MANAGER")
                .pathMatchers("PUT", "/api/events/**").hasAnyRole("ADMIN", "ARTIST_MANAGER")
                .pathMatchers("DELETE", "/api/events/**").hasRole("ADMIN")
                
                // Stage Service endpoints - GET permite acces fără autentificare pentru testare
                .pathMatchers("GET", "/api/stages/**").permitAll()
                .pathMatchers("POST", "/api/stages/**").hasAnyRole("ADMIN", "ARTIST_MANAGER")
                .pathMatchers("PUT", "/api/stages/**").hasAnyRole("ADMIN", "ARTIST_MANAGER")
                .pathMatchers("DELETE", "/api/stages/**").hasRole("ADMIN")
                
                // Ticket Service endpoints - GET permite acces fără autentificare pentru testare
                .pathMatchers("GET", "/api/tickets/**").permitAll()
                .pathMatchers("POST", "/api/tickets/**").hasAnyRole("ADMIN", "TICKET_MANAGER")
                .pathMatchers("PUT", "/api/tickets/**").hasAnyRole("ADMIN", "TICKET_MANAGER")
                .pathMatchers("DELETE", "/api/tickets/**").hasRole("ADMIN")
                
                .anyExchange().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/swagger-ui.html"))
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public OidcReactiveOAuth2UserService oidcUserService() {
        OidcReactiveOAuth2UserService delegate = new OidcReactiveOAuth2UserService();
        
        return new OidcReactiveOAuth2UserService() {
            @Override
            public Mono<OidcUser> loadUser(OidcUserRequest userRequest) {
                OAuth2AccessToken accessToken = userRequest.getAccessToken();
                
                return delegate.loadUser(userRequest)
                    .flatMap(oidcUser -> {
                        String email = oidcUser.getEmail();
                        
                        return iamService.getUserRoles(email, accessToken)
                            .flatMap(iamRoles -> {
                                Set<String> appRoles;
                                
                                if (iamRoles == null || iamRoles.isEmpty()) {
                                    appRoles = iamService.getDefaultRolesForEmail(email);
                                } else {
                                    appRoles = iamService.mapIamRolesToApplicationRoles(iamRoles);
                                    // Adaugă și rolurile default pentru a asigura compatibilitate
                                    appRoles.addAll(iamService.getDefaultRolesForEmail(email));
                                }
                                
                                Set<SimpleGrantedAuthority> authorities = appRoles.stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toSet());
                                
                                return Mono.just(new DefaultOidcUser(
                                    authorities,
                                    oidcUser.getIdToken(),
                                    oidcUser.getUserInfo()
                                ));
                            })
                            .onErrorResume(error -> {
                                logger.error("Error loading IAM roles, using default roles", error);
                                Set<String> appRoles = iamService.getDefaultRolesForEmail(email);
                                
                                Set<SimpleGrantedAuthority> authorities = appRoles.stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toSet());
                                
                                return Mono.just(new DefaultOidcUser(
                                    authorities,
                                    oidcUser.getIdToken(),
                                    oidcUser.getUserInfo()
                                ));
                            });
                    });
            }
        };
    }
}
