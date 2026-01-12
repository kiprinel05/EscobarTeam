package org.example.client;

import org.example.dto.TicketInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "TICKET-SERVICE", path = "/api/tickets")
public interface TicketServiceClient {
    
    @GetMapping("/festival/{eventName}/available-seats")
    Integer getAvailableSeats(
            @PathVariable("eventName") String eventName,
            @RequestHeader("X-Requested-With") String gatewayHeader
    );
    
    @GetMapping("/revenue/by-festival")
    Map<String, Double> getRevenueByFestival(
            @RequestHeader("X-Requested-With") String gatewayHeader
    );
    
    @GetMapping("/festival/{eventName}")
    TicketInfoDTO getTicketInfo(
            @PathVariable("eventName") String eventName,
            @RequestHeader("X-Requested-With") String gatewayHeader,
            @RequestHeader(value = "X-Region", required = false) String region
    );
}
