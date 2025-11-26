package org.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Custom filter that adds region-related headers (CORS and region info)
 * This demonstrates adding headers related to content, region, and origins
 */
@Component
public class CustomRegionFilter extends AbstractGatewayFilterFactory<CustomRegionFilter.Config> {

    public CustomRegionFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-Region", config.getRegion())
                    .header("X-Content-Language", config.getContentLanguage())
                    .header("X-Origin-Region", config.getOriginRegion())
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build())
                    .then(Mono.fromRunnable(() -> {
                        ServerHttpResponse response = exchange.getResponse();
                        response.getHeaders().add("Access-Control-Allow-Origin", "*");
                        response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                        response.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Request-ID");
                        response.getHeaders().add("Access-Control-Max-Age", "3600");
                        response.getHeaders().add("X-Region-Processed", config.getRegion());
                        response.getHeaders().add("X-Content-Location", config.getContentLocation());
                    }));
        };
    }

    public static class Config {
        private String region = "EU-RO";
        private String contentLanguage = "ro-RO";
        private String originRegion = "Romania";
        private String contentLocation = "Europe/Romania";

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getContentLanguage() {
            return contentLanguage;
        }

        public void setContentLanguage(String contentLanguage) {
            this.contentLanguage = contentLanguage;
        }

        public String getOriginRegion() {
            return originRegion;
        }

        public void setOriginRegion(String originRegion) {
            this.originRegion = originRegion;
        }

        public String getContentLocation() {
            return contentLocation;
        }

        public void setContentLocation(String contentLocation) {
            this.contentLocation = contentLocation;
        }
    }
}
