package org.example.gateway.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for viewing authentication token and user information
 */
@RestController
public class TokenController {
    @GetMapping("/id-token")
    public Mono<Map<String, Object>> getIdToken(@AuthenticationPrincipal Mono<OidcUser> oidcUserMono) {
        return oidcUserMono
            .map(oidcUser -> {
                Map<String, Object> info = new HashMap<>();
                info.put("idToken", oidcUser.getIdToken().getTokenValue());
                info.put("claims", oidcUser.getClaims());
                info.put("authorities", oidcUser.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.toList()));
                info.put("email", oidcUser.getEmail());
                info.put("name", oidcUser.getFullName());
                info.put("subject", oidcUser.getSubject());
                return info;
            })
            .defaultIfEmpty(createErrorResponse("User not authenticated"));
    }

    private Map<String, Object> createErrorResponse(String error) {
        Map<String, Object> info = new HashMap<>();
        info.put("error", error);
        return info;
    }
}

