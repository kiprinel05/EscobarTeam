package org.example.gateway.auth;

import org.example.gateway.service.IamRoleService;
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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final String projectId = "task-2-event-479608";
    private final IamRoleService iamRoleService = new IamRoleService(projectId);

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(successHandler()))
                .oauth2Client(Customizer.withDefaults())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/role-mapping").authenticated()
                        
                        .pathMatchers(HttpMethod.POST, "/api/events/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/events/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/events/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/events/**").authenticated()
                        
                        .pathMatchers(HttpMethod.POST, "/api/stages/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/stages/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/stages/**").hasRole("ADMIN")
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
                            System.out.println("Loading user with email: " + email);

                            try {
                                // Obține rolurile IAM din Google Cloud
                                Set<GrantedAuthority> iamRoles = iamRoleService.getIamRoles(userRequest, oidcUser);
                                mappedAuthorities.addAll(iamRoles);
                                System.out.println("IAM roles retrieved: " + iamRoles);
                            } catch (GeneralSecurityException | IOException e) {
                                System.err.println("Error retrieving IAM roles: " + e.getMessage());
                                e.printStackTrace();
                                // Fallback: dacă nu se pot obține rolurile IAM, folosește logica veche
                                if (email != null) {
                                    if (email.endsWith("@gmail.com")) {
                                        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                                    }
                                    if (email.endsWith("@festival-admin.ro")) {
                                        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                                    }
                                }
                            }

                            System.out.println("Mapped authorities for " + email + ": " + mappedAuthorities);

                            return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
                        });
            }
        };
    }

}

