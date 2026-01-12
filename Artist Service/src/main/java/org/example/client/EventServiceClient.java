package org.example.client;

import org.example.dto.EventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "EVENT-SERVICE", path = "/api/events")
public interface EventServiceClient {
    
    @GetMapping("/search")
    List<EventDTO> searchEventsByArtist(
            @RequestParam("name") String artistName,
            @RequestHeader("X-Requested-With") String gatewayHeader,
            @RequestHeader(value = "X-Region", required = false) String region,
            @RequestHeader(value = "X-Content-Language", required = false) String language
    );
    
    @GetMapping("/filter/artist")
    List<EventDTO> filterEventsByArtist(
            @RequestParam("artist") String artistName,
            @RequestHeader("X-Requested-With") String gatewayHeader,
            @RequestHeader(value = "X-Region", required = false) String region,
            @RequestHeader(value = "X-Content-Language", required = false) String language
    );
}
