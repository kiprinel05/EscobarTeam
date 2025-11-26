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
                // Public endpoints
                .pathMatchers("/actuator/**", "/login/**", "/oauth2/**", "/id-token").permitAll()
                
                // Artist Service - GET endpoints (read access for all authenticated users)
                .pathMatchers("GET", "/api/artists/**").hasAnyRole("USER", "ADMIN", "ARTIST_MANAGER")
                
                // Artist Service - POST endpoints (create - only ADMIN and ARTIST_MANAGER)
                .pathMatchers("POST", "/api/artists/**").hasAnyRole("ADMIN", "ARTIST_MANAGER")
                
                // Artist Service - PUT endpoints (update - only ADMIN and ARTIST_MANAGER)
                .pathMatchers("PUT", "/api/artists/**").hasAnyRole("ADMIN", "ARTIST_MANAGER")
                
                // Artist Service - DELETE endpoints (delete - only ADMIN)
                .pathMatchers("DELETE", "/api/artists/**").hasRole("ADMIN")
                
                // All other requests require authentication
                .anyExchange().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/id-token"))
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
                return delegate.loadUser(userRequest)
                    .map(oidcUser -> {
                        String email = oidcUser.getEmail();
                        
                        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
                        
                        // Default role for all authenticated users
                        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                        
                        // Admin role for specific emails
                        if (email != null && (email.endsWith("@admin.com") || email.equals("ciprian.dumitrasc@gmail.com"))) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                        }
                        
                        // Artist Manager role for specific emails
                        if (email != null && (email.endsWith("@manager.com") || email.equals("sweetvip2017@gmail.com"))) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_ARTIST_MANAGER"));
                        }
                        
                        return new DefaultOidcUser(
                            authorities,
                            oidcUser.getIdToken(),
                            oidcUser.getUserInfo()
                        );
                    });
            }
        };
    }
}

