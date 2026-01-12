package org.example.ticketservice.client;

import org.example.ticketservice.dto.EventDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "EVENT-SERVICE", path = "/api/events")
public interface EventServiceClient {
    
    @GetMapping("/search")
    List<EventDetailsDTO> searchEventsByName(
            @RequestParam("name") String eventName,
            @RequestHeader("X-Requested-With") String gatewayHeader
    );
    
    @GetMapping("/{id}")
    EventDetailsDTO getEventById(
            @PathVariable("id") Long id,
            @RequestHeader("X-Requested-With") String gatewayHeader
    );
}
