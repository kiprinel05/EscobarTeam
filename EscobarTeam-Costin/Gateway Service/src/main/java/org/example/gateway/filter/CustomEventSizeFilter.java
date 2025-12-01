package org.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Custom filter that adds event size information to responses
 */
@Component
public class CustomEventSizeFilter extends AbstractGatewayFilterFactory<CustomEventSizeFilter.Config> {

    private static final String X_EVENT_SIZE = "X-Event-Size";

    public CustomEventSizeFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        ServerHttpResponse response = exchange.getResponse();
                        // Adaugă header pentru event size (poate fi calculat din response body dacă e necesar)
                        response.getHeaders().add(X_EVENT_SIZE, "calculated");
                    }));
        };
    }

    public static class Config {
        // Configurație opțională pentru filter
    }
}

