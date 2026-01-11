package org.example.ticketservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EventServiceClient {

    @Autowired
    @Qualifier("directRestTemplate")
    private RestTemplate restTemplate;

    @Value("${services.event-service.url:http://EVENT-SERVICE}")
    private String eventServiceUrl;

    public EventResponseDTO getEventByName(String eventName) {
        try {
            List<EventResponseDTO> allEvents = getAllEvents();
            if (allEvents != null && !allEvents.isEmpty()) {
                return allEvents.stream()
                        .filter(e -> e.getName() != null && e.getName().equalsIgnoreCase(eventName))
                        .findFirst()
                        .orElse(null);
            }
            return null;
        } catch (Exception e) {
            try {
                String encodedName = java.net.URLEncoder.encode(eventName, java.nio.charset.StandardCharsets.UTF_8);
                String url = eventServiceUrl + "/api/events/search?name=" + encodedName;
                HttpHeaders headers = new HttpHeaders();
                headers.set("X-Requested-With", "Gateway-Service");
                HttpEntity<String> entity = new HttpEntity<>(headers);
                
                ResponseEntity<List<EventResponseDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<EventResponseDTO>>() {}
                );
                
                List<EventResponseDTO> events = response.getBody();
                if (events != null && !events.isEmpty()) {
                    return events.stream()
                            .filter(event -> event.getName() != null && event.getName().equalsIgnoreCase(eventName))
                            .findFirst()
                            .orElse(null);
                }
            } catch (Exception ex) {
            }
            return null;
        }
    }

    public List<EventResponseDTO> getAllEvents() {
        String url = eventServiceUrl + "/api/events";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Requested-With", "Gateway-Service");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<List<EventResponseDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<EventResponseDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            return List.of();
        }
    }

    public static class EventResponseDTO {
        private Long id;
        private String name;
        private LocalDateTime date;
        private Long stageId;
        private String stageName;
        private String associatedArtist;
        private Integer capacity;
        private LocalDateTime createdAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
        public Long getStageId() { return stageId; }
        public void setStageId(Long stageId) { this.stageId = stageId; }
        public String getStageName() { return stageName; }
        public void setStageName(String stageName) { this.stageName = stageName; }
        public String getAssociatedArtist() { return associatedArtist; }
        public void setAssociatedArtist(String associatedArtist) { this.associatedArtist = associatedArtist; }
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}

