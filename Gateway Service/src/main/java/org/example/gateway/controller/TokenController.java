package org.example.gateway.controller;

import org.example.gateway.service.GoogleCloudIAMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class TokenController {

    @Autowired
    private GoogleCloudIAMService iamService;

    @GetMapping("/id-token")
    public Mono<Map<String, Object>> getIdToken() {
        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .cast(Authentication.class)
            .map(Authentication::getPrincipal)
            .cast(OidcUser.class)
            .map(oidcUser -> buildUserInfo(oidcUser))
            .defaultIfEmpty(createErrorResponse("User not authenticated"));
    }

    @GetMapping("/role-mapping")
    public Mono<Map<String, Object>> getRoleMapping() {
        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .cast(Authentication.class)
            .map(Authentication::getPrincipal)
            .cast(OidcUser.class)
            .map(oidcUser -> {
                Map<String, Object> mapping = new HashMap<>();
                String email = oidcUser.getEmail();
                mapping.put("email", email);
                mapping.put("name", oidcUser.getFullName());
                
                Set<String> appRoles = oidcUser.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toSet());
                mapping.put("applicationRoles", appRoles);
                
                Set<String> fallbackRoles = iamService.getDefaultRolesForEmail(email);
                mapping.put("fallbackRoles", fallbackRoles);
                
                return mapping;
            })
            .defaultIfEmpty(createErrorResponse("User not authenticated"));
    }

    private Map<String, Object> buildUserInfo(OidcUser oidcUser) {
        Map<String, Object> info = new HashMap<>();
        String email = oidcUser.getEmail();
        info.put("email", email);
        info.put("name", oidcUser.getFullName());
        info.put("subject", oidcUser.getSubject());
        
        Set<String> appRoles = oidcUser.getAuthorities().stream()
            .map(auth -> auth.getAuthority())
            .collect(Collectors.toSet());
        info.put("applicationRoles", appRoles);
        
        Set<String> fallbackRoles = iamService.getDefaultRolesForEmail(email);
        info.put("fallbackRoles", fallbackRoles);
        
        String tokenValue = oidcUser.getIdToken().getTokenValue();
        if (tokenValue != null && tokenValue.length() > 50) {
            info.put("idTokenPreview", tokenValue.substring(0, 50) + "...");
        }
        
        return info;
    }

    private Map<String, Object> createErrorResponse(String error) {
        Map<String, Object> info = new HashMap<>();
        info.put("error", error);
        return info;
    }
}
