package org.example.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JwtRoleConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private static final Logger logger = LoggerFactory.getLogger(JwtRoleConverter.class);

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractRoles(jwt);
        AbstractAuthenticationToken auth = new JwtAuthenticationToken(jwt, authorities);
        return Mono.just(auth);
    }

    private Collection<GrantedAuthority> extractRoles(Jwt jwt) {
        // Log all claims to debug what we are receiving
        logger.info("JWT Claims: {}", jwt.getClaims());

        List<String> roles = jwt.getClaimAsStringList("roles");

        if (roles == null) {
            logger.warn("No 'roles' claim found in JWT.");
            return Collections.emptyList();
        }

        return roles.stream()
                .map(r -> "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
