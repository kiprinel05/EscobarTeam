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

    private static final String X_REQUEST_ID = "X-Request-ID";
    private static final String X_GATEWAY_SERVICE = "X-Gateway-Service";
    private static final String X_RESPONSE_ID = "X-Response-ID";
    private static final String X_PROCESSED_BY = "X-Processed-By";
    private static final String GATEWAY_SERVICE_NAME = "Gateway-Service";

    public CustomHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
                    .header(X_GATEWAY_SERVICE, GATEWAY_SERVICE_NAME)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build())
                    .then(Mono.fromRunnable(() -> {
                        ServerHttpResponse response = exchange.getResponse();
                        response.getHeaders().add(X_RESPONSE_ID, UUID.randomUUID().toString());
                        response.getHeaders().add(X_PROCESSED_BY, GATEWAY_SERVICE_NAME);
                    }));
        };
    }

    public static class Config {
    }
}

