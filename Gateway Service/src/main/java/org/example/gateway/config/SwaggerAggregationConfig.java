package org.example.gateway.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerAggregationConfig {

    @Bean
    public List<GroupedOpenApi> apis(RouteLocator routeLocator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        
        // Artist Service
        groups.add(GroupedOpenApi.builder()
                .group("artists")
                .pathsToMatch("/api/artists/**")
                .build());
        
        // Event Service
        groups.add(GroupedOpenApi.builder()
                .group("events")
                .pathsToMatch("/api/events/**")
                .build());
        
        // Stage Service
        groups.add(GroupedOpenApi.builder()
                .group("stages")
                .pathsToMatch("/api/stages/**")
                .build());
        
        // Ticket Service
        groups.add(GroupedOpenApi.builder()
                .group("tickets")
                .pathsToMatch("/api/tickets/**")
                .build());
        
        return groups;
    }
}
