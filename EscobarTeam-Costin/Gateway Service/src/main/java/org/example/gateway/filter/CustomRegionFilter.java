package org.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Custom filter that adds region-related headers to requests and responses
 */
@Component
public class CustomRegionFilter extends AbstractGatewayFilterFactory<CustomRegionFilter.Config> {

    private static final String X_REGION = "X-Region";
    private static final String X_CONTENT_LANGUAGE = "X-Content-Language";
    private static final String X_ORIGIN_REGION = "X-Origin-Region";
    private static final String X_CONTENT_LOCATION = "X-Content-Location";

    public CustomRegionFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(X_REGION, config.getRegion())
                    .header(X_CONTENT_LANGUAGE, config.getContentLanguage())
                    .header(X_ORIGIN_REGION, config.getOriginRegion())
                    .header(X_CONTENT_LOCATION, config.getContentLocation())
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build())
                    .then(Mono.fromRunnable(() -> {
                        ServerHttpResponse response = exchange.getResponse();
                        response.getHeaders().add(X_REGION, config.getRegion());
                        response.getHeaders().add(X_CONTENT_LANGUAGE, config.getContentLanguage());
                        response.getHeaders().add(X_ORIGIN_REGION, config.getOriginRegion());
                        response.getHeaders().add(X_CONTENT_LOCATION, config.getContentLocation());
                    }));
        };
    }

    public static class Config {
        private String region;
        private String contentLanguage;
        private String originRegion;
        private String contentLocation;

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

