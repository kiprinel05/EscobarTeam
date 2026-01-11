package org.example.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class TicketServiceClient {

    @Autowired
    @Qualifier("directRestTemplate")
    private RestTemplate restTemplate;

    @Value("${services.ticket-service.url:http://TICKET-SERVICE}")
    private String ticketServiceUrl;

    public Integer getAvailableSeatsForEvent(String eventName) {
        try {
            String encodedEventName = java.net.URLEncoder.encode(eventName, java.nio.charset.StandardCharsets.UTF_8);
            String url = ticketServiceUrl + "/api/tickets/festival/" + encodedEventName + "/available-seats";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Requested-With", "Gateway-Service");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Integer> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Integer.class
            );
            return response.getBody() != null ? response.getBody() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public Double getRevenueForEvent(String eventName) {
        String url = ticketServiceUrl + "/api/tickets/revenue/by-festival";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Requested-With", "Gateway-Service");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map<String, Double>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Double>>() {}
            );
            Map<String, Double> revenue = response.getBody();
            return revenue != null ? revenue.getOrDefault(eventName, 0.0) : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
}

