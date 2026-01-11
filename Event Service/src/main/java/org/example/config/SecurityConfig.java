package org.example.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Permit Swagger UI and Actuator endpoints
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/actuator/**"
                ).permitAll()
                // Require Gateway header for API endpoints
                .requestMatchers("/api/**").permitAll()
                .anyRequest().permitAll()
            )
            .addFilterBefore(new GatewayHeaderFilter(), BasicAuthenticationFilter.class)
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(formLogin -> formLogin.disable());

        return http.build();
    }

    /**
     * Filter that checks if request comes from Gateway
     */
    private static class GatewayHeaderFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
                throws ServletException, IOException {
            String path = request.getRequestURI();
            
            // Allow Swagger UI and Actuator without Gateway header
            if (path.startsWith("/swagger-ui") || 
                path.startsWith("/v3/api-docs") || 
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars") ||
                path.startsWith("/actuator")) {
                filterChain.doFilter(request, response);
                return;
            }

            // For API endpoints, require Gateway header
            if (path.startsWith("/api/")) {
                String gatewayHeader = request.getHeader("X-Requested-With");
                if (gatewayHeader == null || !gatewayHeader.equals("Gateway-Service")) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Access denied. Requests must come through Gateway.\"}");
                    return;
                }
            }

            filterChain.doFilter(request, response);
        }
    }
}
