package org.example.ticketservice.service;

import org.example.ticketservice.client.EventServiceClient;
import org.example.ticketservice.dto.EventDetailsDTO;
import org.example.ticketservice.dto.TicketCreateDTO;
import org.example.ticketservice.dto.TicketDTO;
import org.example.ticketservice.dto.TicketWithEventDetailsDTO;
import org.example.ticketservice.exception.TicketNotFoundException;
import org.example.ticketservice.mapper.TicketMapper;
import org.example.ticketservice.model.Ticket;
import org.example.ticketservice.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketMapper ticketMapper;

    @Mock
    private EventServiceClient eventServiceClient;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Ticket ticket;
    private TicketDTO ticketDTO;
    private TicketCreateDTO ticketCreateDTO;

    @BeforeEach
    void setUp() {
        ticket = Ticket.builder()
                .id(1L)
                .eventName("Summer Festival")
                .ticketType("VIP")
                .price(100.0)
                .quantity(2)
                .buyerName("John Doe")
                .buyerEmail("john@email.com")
                .purchaseDate(LocalDateTime.now())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        ticketDTO = TicketDTO.builder()
                .id(1L)
                .eventName("Summer Festival")
                .ticketType("VIP")
                .price(100.0)
                .quantity(2)
                .buyerName("John Doe")
                .buyerEmail("john@email.com")
                .purchaseDate(LocalDateTime.now())
                .isActive(true)
                .build();

        ticketCreateDTO = TicketCreateDTO.builder()
                .eventName("Summer Festival")
                .ticketType("VIP")
                .price(100.0)
                .quantity(2)
                .buyerName("John Doe")
                .buyerEmail("john@email.com")
                .build();
    }

    @Test
    void testGetAllTickets_Success() {
        // Given
        when(ticketRepository.findAll()).thenReturn(Collections.singletonList(ticket));
        when(ticketMapper.toDTO(ticket)).thenReturn(ticketDTO);

        // When
        List<TicketDTO> result = ticketService.getAllTickets();

        // Then
        assertEquals(1, result.size());
        verify(ticketRepository).findAll();
    }

    @Test
    void testGetAllTickets_Empty() {
        // Given
        when(ticketRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<TicketDTO> result = ticketService.getAllTickets();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTicketById_Success() {
        // Given
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketMapper.toDTO(ticket)).thenReturn(ticketDTO);

        // When
        TicketDTO result = ticketService.getTicketById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetTicketById_NotFound() {
        // Given
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TicketNotFoundException.class, () -> ticketService.getTicketById(99L));
    }

    @Test
    void testCreateTicket_Success() {
        // Given
        when(ticketMapper.toEntity(ticketCreateDTO)).thenReturn(ticket);
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(ticketMapper.toDTO(ticket)).thenReturn(ticketDTO);

        // When
        TicketDTO result = ticketService.createTicket(ticketCreateDTO);

        // Then
        assertNotNull(result);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void testUpdateTicket_Success() {
        // Given
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(ticketMapper.toDTO(ticket)).thenReturn(ticketDTO);

        // When
        TicketDTO result = ticketService.updateTicket(1L, ticketDTO);

        // Then
        assertNotNull(result);
        verify(ticketMapper).updateEntityFromDTO(eq(ticketDTO), eq(ticket));
    }

    @Test
    void testUpdateTicket_NotFound() {
        // Given
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TicketNotFoundException.class, () -> ticketService.updateTicket(99L, ticketDTO));
    }

    @Test
    void testDeleteTicket_Success() {
        // Given
        when(ticketRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ticketRepository).deleteById(1L);

        // When
        assertDoesNotThrow(() -> ticketService.deleteTicket(1L));

        // Then
        verify(ticketRepository).deleteById(1L);
    }

    @Test
    void testDeleteTicket_NotFound() {
        // Given
        when(ticketRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThrows(TicketNotFoundException.class, () -> ticketService.deleteTicket(99L));
    }

    @Test
    void testGetTicketsByFestival_Success() {
        // Given
        when(ticketRepository.findByEventName("Summer Festival"))
                .thenReturn(Collections.singletonList(ticket));
        when(ticketMapper.toDTO(ticket)).thenReturn(ticketDTO);

        // When
        List<TicketDTO> result = ticketService.getTicketsByFestival("Summer Festival");

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void testGetAvailableSeats_Success() {
        // Given
        when(ticketRepository.countByEventName("Summer Festival")).thenReturn(100);

        // When
        int result = ticketService.getAvailableSeats("Summer Festival");

        // Then
        assertEquals(100, result);
    }

    @Test
    void testGetRevenueByFestival_Success() {
        // Given
        Ticket ticket2 = Ticket.builder()
                .eventName("Winter Concert")
                .price(50.0)
                .quantity(3)
                .build();
        when(ticketRepository.findAll()).thenReturn(Arrays.asList(ticket, ticket2));

        // When
        Map<String, Double> result = ticketService.getRevenueByFestival();

        // Then
        assertEquals(2, result.size());
        assertEquals(200.0, result.get("Summer Festival"));
        assertEquals(150.0, result.get("Winter Concert"));
    }

    @Test
    void testGetRevenueByFestival_Empty() {
        // Given
        when(ticketRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        Map<String, Double> result = ticketService.getRevenueByFestival();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTicketsByType_Success() {
        // Given
        when(ticketRepository.findByTicketType("VIP"))
                .thenReturn(Collections.singletonList(ticket));
        when(ticketMapper.toDTO(ticket)).thenReturn(ticketDTO);

        // When
        List<TicketDTO> result = ticketService.getTicketsByType("VIP");

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void testGetTotalRevenue_Success() {
        // Given
        when(ticketRepository.findAll()).thenReturn(Collections.singletonList(ticket));

        // When
        Double result = ticketService.getTotalRevenue();

        // Then
        assertEquals(200.0, result);
    }

    @Test
    void testGetTotalRevenue_Empty() {
        // Given
        when(ticketRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        Double result = ticketService.getTotalRevenue();

        // Then
        assertEquals(0.0, result);
    }

    @Test
    void testGetTicketWithEventDetails_Success() {
        // Given
        EventDetailsDTO eventDetails = new EventDetailsDTO();
        eventDetails.setName("Summer Festival");
        eventDetails.setDate(LocalDateTime.now().plusDays(30));
        eventDetails.setStageName("Main Stage");
        eventDetails.setAssociatedArtist("Famous Artist");
        eventDetails.setCapacity(5000);

        when(ticketRepository.findByEventName("Summer Festival"))
                .thenReturn(Collections.singletonList(ticket));
        when(ticketMapper.toDTO(ticket)).thenReturn(ticketDTO);
        when(eventServiceClient.searchEventsByName(anyString(), anyString()))
                .thenReturn(Collections.singletonList(eventDetails));

        // When
        TicketWithEventDetailsDTO result = ticketService.getTicketWithEventDetails("Summer Festival", "ro");

        // Then
        assertNotNull(result);
        assertEquals("Summer Festival", result.getEventName());
        assertEquals("Main Stage", result.getStageName());
    }

    @Test
    void testGetTicketWithEventDetails_NoTickets() {
        // Given
        when(ticketRepository.findByEventName("Unknown")).thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(RuntimeException.class, 
                () -> ticketService.getTicketWithEventDetails("Unknown", "ro"));
    }

    @Test
    void testGetTicketWithEventDetails_EnglishMessage() {
        // Given
        EventDetailsDTO eventDetails = new EventDetailsDTO();
        eventDetails.setName("Summer Festival");
        eventDetails.setDate(LocalDateTime.now().plusDays(30));
        eventDetails.setStageName("Main Stage");

        when(ticketRepository.findByEventName("Summer Festival"))
                .thenReturn(Collections.singletonList(ticket));
        when(ticketMapper.toDTO(ticket)).thenReturn(ticketDTO);
        when(eventServiceClient.searchEventsByName(anyString(), anyString()))
                .thenReturn(Collections.singletonList(eventDetails));

        // When
        TicketWithEventDetailsDTO result = ticketService.getTicketWithEventDetails("Summer Festival", "en-US");

        // Then
        assertTrue(result.getValidationMessage().contains("validated"));
    }

    @Test
    void testPurchaseTicketWithValidation_USRegion() {
        // Given
        EventDetailsDTO eventDetails = new EventDetailsDTO();
        eventDetails.setName("Summer Festival");

        when(eventServiceClient.searchEventsByName(anyString(), anyString()))
                .thenReturn(Collections.singletonList(eventDetails));
        when(ticketMapper.toEntity(any(TicketCreateDTO.class))).thenReturn(ticket);
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(ticketMapper.toDTO(ticket)).thenReturn(ticketDTO);

        // When
        TicketDTO result = ticketService.purchaseTicketWithValidation(ticketCreateDTO, "US", "en");

        // Then
        assertNotNull(result);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void testPurchaseTicketWithValidation_RORegion() {
        // Given
        EventDetailsDTO eventDetails = new EventDetailsDTO();
        eventDetails.setName("Summer Festival");

        when(eventServiceClient.searchEventsByName(anyString(), anyString()))
                .thenReturn(Collections.singletonList(eventDetails));
        when(ticketMapper.toEntity(any(TicketCreateDTO.class))).thenReturn(ticket);
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(ticketMapper.toDTO(ticket)).thenReturn(ticketDTO);

        // When
        TicketDTO result = ticketService.purchaseTicketWithValidation(ticketCreateDTO, "EU-RO", "ro");

        // Then
        assertNotNull(result);
    }

    @Test
    void testPurchaseTicketWithValidation_EventNotFound() {
        // Given
        when(eventServiceClient.searchEventsByName(anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(RuntimeException.class,
                () -> ticketService.purchaseTicketWithValidation(ticketCreateDTO, "EU", "ro"));
    }
}
