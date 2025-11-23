package org.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Custom filter that adds a unique request ID header to requests
 * and a response time header to responses
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
                    .header("X-Request-ID", UUID.randomUUID().toString())
                    .header("X-Gateway-Service", "Gateway-Service")
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build())
                    .then(Mono.fromRunnable(() -> {
                        ServerHttpResponse response = exchange.getResponse();
                        response.getHeaders().add("X-Response-ID", UUID.randomUUID().toString());
                        response.getHeaders().add("X-Processed-By", "Gateway-Service");
                    }));
        };
    }

    public static class Config {
    }
}

