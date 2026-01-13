package org.example.service;

import org.example.client.TicketServiceClient;
import org.example.dto.*;
import org.example.entity.Event;
import org.example.entity.Stage;
import org.example.mapper.EventMapper;
import org.example.repository.EventRepository;
import org.example.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private TicketServiceClient ticketServiceClient;

    @InjectMocks
    private EventServiceImpl eventService;

    private Stage stage;
    private Event event;
    private EventResponseDTO eventResponseDTO;
    private EventCreateDTO eventCreateDTO;
    private EventDTO eventDTO;

    @BeforeEach
    void setUp() {
        stage = new Stage();
        stage.setId(1L);
        stage.setName("Main Stage");
        stage.setLocation("Arena A");
        stage.setMaxCapacity(5000);

        event = new Event();
        event.setId(1L);
        event.setName("Summer Festival");
        event.setDate(LocalDateTime.of(2026, 7, 15, 20, 0));
        event.setStage(stage);
        event.setAssociatedArtist("Famous Artist");
        event.setCapacity(3000);
        event.setCreatedAt(LocalDateTime.now());

        eventResponseDTO = new EventResponseDTO();
        eventResponseDTO.setId(1L);
        eventResponseDTO.setName("Summer Festival");
        eventResponseDTO.setDate(LocalDateTime.of(2026, 7, 15, 20, 0));
        eventResponseDTO.setStageId(1L);
        eventResponseDTO.setStageName("Main Stage");
        eventResponseDTO.setAssociatedArtist("Famous Artist");
        eventResponseDTO.setCapacity(3000);

        eventCreateDTO = EventCreateDTO.builder()
                .name("Summer Festival")
                .date(LocalDateTime.of(2026, 7, 15, 20, 0))
                .stageId(1L)
                .associatedArtist("Famous Artist")
                .capacity(3000)
                .build();

        eventDTO = new EventDTO();
        eventDTO.setId(1L);
        eventDTO.setName("Updated Festival");
        eventDTO.setDate(LocalDateTime.of(2026, 7, 16, 21, 0));
        eventDTO.setStageId(1L);
        eventDTO.setAssociatedArtist("Another Artist");
        eventDTO.setCapacity(4000);
    }

    @Test
    void testGetAllEvents_Success() {
        // Given
        when(eventRepository.findAll()).thenReturn(Arrays.asList(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        List<EventResponseDTO> result = eventService.getAllEvents();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository).findAll();
    }

    @Test
    void testGetAllEvents_Empty() {
        // Given
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<EventResponseDTO> result = eventService.getAllEvents();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetEventById_Success() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        EventResponseDTO result = eventService.getEventById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(eventRepository).findById(1L);
    }

    @Test
    void testGetEventById_NotFound() {
        // Given
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> eventService.getEventById(99L));
    }

    @Test
    void testCreateEvent_Success() {
        // Given
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(eventRepository.findConflictingEvents(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(eventMapper.toEntity(eventCreateDTO, stage)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        EventResponseDTO result = eventService.createEvent(eventCreateDTO);

        // Then
        assertNotNull(result);
        assertEquals("Summer Festival", result.getName());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void testCreateEvent_StageNotFound() {
        // Given
        when(stageRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> eventService.createEvent(eventCreateDTO));
    }

    @Test
    void testCreateEvent_ConflictingEvent() {
        // Given
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(eventRepository.findConflictingEvents(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(event));

        // When & Then
        assertThrows(RuntimeException.class, () -> eventService.createEvent(eventCreateDTO));
    }

    @Test
    void testUpdateEvent_Success() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        EventResponseDTO result = eventService.updateEvent(1L, eventDTO);

        // Then
        assertNotNull(result);
        verify(eventMapper).updateEntityFromDTO(eq(eventDTO), eq(event), eq(stage));
    }

    @Test
    void testUpdateEvent_NotFound() {
        // Given
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> eventService.updateEvent(99L, eventDTO));
    }

    @Test
    void testDeleteEvent_Success() {
        // Given
        when(eventRepository.existsById(1L)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(1L);

        // When
        assertDoesNotThrow(() -> eventService.deleteEvent(1L));

        // Then
        verify(eventRepository).deleteById(1L);
    }

    @Test
    void testDeleteEvent_NotFound() {
        // Given
        when(eventRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> eventService.deleteEvent(99L));
    }

    @Test
    void testSearchByName_Success() {
        // Given
        when(eventRepository.findByNameContainingIgnoreCase("Summer"))
                .thenReturn(Collections.singletonList(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        List<EventResponseDTO> result = eventService.searchByName("Summer");

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void testFilterByStage_Success() {
        // Given
        when(eventRepository.findByStageId(1L)).thenReturn(Collections.singletonList(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        List<EventResponseDTO> result = eventService.filterByStage(1L);

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void testFilterByDate_Success() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 12, 31, 23, 59);
        when(eventRepository.findByDateBetween(start, end))
                .thenReturn(Collections.singletonList(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        List<EventResponseDTO> result = eventService.filterByDate(start, end);

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void testFilterBySpecificDate_Success() {
        // Given
        LocalDate date = LocalDate.of(2026, 7, 15);
        when(eventRepository.findByDateBetween(any(), any()))
                .thenReturn(Collections.singletonList(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        List<EventResponseDTO> result = eventService.filterBySpecificDate(date);

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void testFilterByCapacity_Success() {
        // Given
        when(eventRepository.findByCapacityGreaterThanEqual(1000))
                .thenReturn(Collections.singletonList(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        List<EventResponseDTO> result = eventService.filterByCapacity(1000);

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void testSortByDate_Ascending() {
        // Given
        when(eventRepository.findAll()).thenReturn(Collections.singletonList(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        List<EventResponseDTO> result = eventService.sortByDate("asc");

        // Then
        assertFalse(result.isEmpty());
    }

    @Test
    void testSortByDate_Descending() {
        // Given
        when(eventRepository.findAll()).thenReturn(Collections.singletonList(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        List<EventResponseDTO> result = eventService.sortByDate("desc");

        // Then
        assertFalse(result.isEmpty());
    }

    @Test
    void testSortByCapacity_Ascending() {
        // Given
        when(eventRepository.findAll()).thenReturn(Collections.singletonList(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        List<EventResponseDTO> result = eventService.sortByCapacity("asc");

        // Then
        assertFalse(result.isEmpty());
    }

    @Test
    void testCheckStageAvailability_Available() {
        // Given
        LocalDateTime date = LocalDateTime.of(2026, 7, 15, 14, 0);
        when(stageRepository.findAll()).thenReturn(Collections.singletonList(stage));
        when(eventRepository.findConflictingEvents(eq(1L), any(), any()))
                .thenReturn(Collections.emptyList());

        // When
        List<StageAvailabilityDTO> result = eventService.checkStageAvailability(date);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).isAvailable());
    }

    @Test
    void testCheckStageAvailability_NotAvailable() {
        // Given
        LocalDateTime date = LocalDateTime.of(2026, 7, 15, 20, 0);
        when(stageRepository.findAll()).thenReturn(Collections.singletonList(stage));
        when(eventRepository.findConflictingEvents(eq(1L), any(), any()))
                .thenReturn(Collections.singletonList(event));

        // When
        List<StageAvailabilityDTO> result = eventService.checkStageAvailability(date);

        // Then
        assertEquals(1, result.size());
        assertFalse(result.get(0).isAvailable());
    }

    @Test
    void testGetStageSchedule_Success() {
        // Given
        when(eventRepository.findAllByStageIdOrderByDate(1L))
                .thenReturn(Collections.singletonList(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        List<EventResponseDTO> result = eventService.getStageSchedule(1L);

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void testGenerateStatistics_Success() {
        // Given
        when(eventRepository.findAll()).thenReturn(Collections.singletonList(event));

        // When
        EventStatisticsDTO result = eventService.generateStatistics();

        // Then
        assertEquals(1L, result.getTotalEvents());
        assertEquals(3000L, result.getTotalParticipants());
    }

    @Test
    void testGenerateStatistics_Empty() {
        // Given
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        EventStatisticsDTO result = eventService.generateStatistics();

        // Then
        assertEquals(0L, result.getTotalEvents());
        assertEquals(0L, result.getTotalParticipants());
    }

    @Test
    void testFilterByArtist_Success() {
        // Given
        when(eventRepository.findByAssociatedArtistContainingIgnoreCase("Famous"))
                .thenReturn(Collections.singletonList(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);

        // When
        List<EventResponseDTO> result = eventService.filterByArtist("Famous");

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void testGetEventWithTicketInfo_Available() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);
        when(ticketServiceClient.getAvailableSeats(anyString(), anyString())).thenReturn(1000);
        when(ticketServiceClient.getRevenueByFestival(anyString())).thenReturn(Map.of("Summer Festival", 50000.0));

        // When
        EventWithTicketInfoDTO result = eventService.getEventWithTicketInfo(1L, "EU");

        // Then
        assertEquals("AVAILABLE", result.getTicketStatus());
        assertEquals(1000, result.getAvailableSeats());
    }

    @Test
    void testGetEventWithTicketInfo_SoldOut() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);
        when(ticketServiceClient.getAvailableSeats(anyString(), anyString())).thenReturn(0);
        when(ticketServiceClient.getRevenueByFestival(anyString())).thenReturn(Map.of());

        // When
        EventWithTicketInfoDTO result = eventService.getEventWithTicketInfo(1L, "EU");

        // Then
        assertEquals("SOLD_OUT", result.getTicketStatus());
    }

    @Test
    void testGetEventWithTicketInfo_Limited() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);
        when(ticketServiceClient.getAvailableSeats(anyString(), anyString())).thenReturn(50);
        when(ticketServiceClient.getRevenueByFestival(anyString())).thenReturn(Map.of());

        // When
        EventWithTicketInfoDTO result = eventService.getEventWithTicketInfo(1L, "EU");

        // Then
        assertEquals("LIMITED", result.getTicketStatus());
    }

    @Test
    void testGetEventWithTicketInfo_USRegion() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);
        when(ticketServiceClient.getAvailableSeats(anyString(), anyString())).thenReturn(500);
        when(ticketServiceClient.getRevenueByFestival(anyString())).thenReturn(Map.of("Summer Festival", 50000.0));

        // When
        EventWithTicketInfoDTO result = eventService.getEventWithTicketInfo(1L, "US");

        // Then
        assertTrue(result.getMessage().contains("$"));
    }

    @Test
    void testGetEventWithTicketInfo_RORegion() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(eventResponseDTO);
        when(ticketServiceClient.getAvailableSeats(anyString(), anyString())).thenReturn(500);
        when(ticketServiceClient.getRevenueByFestival(anyString())).thenReturn(Map.of("Summer Festival", 50000.0));

        // When
        EventWithTicketInfoDTO result = eventService.getEventWithTicketInfo(1L, "EU-RO");

        // Then
        assertTrue(result.getMessage().contains("RON"));
    }
}
