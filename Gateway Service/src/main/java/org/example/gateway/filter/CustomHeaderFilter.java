package org.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Custom filter specific for Ticket Service routing
 */
@Component
public class CustomHeaderFilter extends AbstractGatewayFilterFactory<CustomHeaderFilter.Config> {

    public CustomHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-Target-System", "Ticket-Core")
                    .header("X-Ticket-Request-Date", Instant.now().toString())
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build())
                    .then(Mono.fromRunnable(() -> {
                        ServerHttpResponse response = exchange.getResponse();
                        response.getHeaders().add("X-Ticket-Filter-Applied", "true");
                    }));
        };
    }

    public static class Config {
    }
}