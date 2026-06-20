package com.duoc.reservams.availabilityservice.service;

import com.duoc.reservams.availabilityservice.dto.AvailabilityCheckRequestDTO;
import com.duoc.reservams.availabilityservice.dto.AvailabilityCheckResponseDTO;
import com.duoc.reservams.availabilityservice.dto.AvailabilityRequestDTO;
import com.duoc.reservams.availabilityservice.dto.AvailabilityResponseDTO;
import com.duoc.reservams.availabilityservice.model.RoomAvailability;
import com.duoc.reservams.availabilityservice.repository.RoomAvailabilityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// pruebas unitarias para AvailabilityService
@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private RoomAvailabilityRepository availabilityRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    @Test
    void findAll_shouldReturnAvailabilityList() {
        // Given
        when(availabilityRepository.findAll()).thenReturn(List.of(
                buildAvailability(1L, LocalDate.of(2026, 7, 10), "AVAILABLE"),
                buildAvailability(2L, LocalDate.of(2026, 7, 11), "BOOKED")
        ));

        // When
        List<AvailabilityResponseDTO> response = availabilityService.findAll();

        // Then
        assertNotNull(response);
        assertEquals(2, response.size());

        verify(availabilityRepository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnAvailability_whenExists() {
        // Given
        RoomAvailability availability = buildAvailability(1L, LocalDate.of(2026, 7, 10), "AVAILABLE");

        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(availability));

        // When
        AvailabilityResponseDTO response = availabilityService.findById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getRoomId());
        assertEquals(LocalDate.of(2026, 7, 10), response.getAvailableDate());
        assertEquals("AVAILABLE", response.getStatus());

        verify(availabilityRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowException_whenAvailabilityNotFound() {
        // Given
        when(availabilityRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> availabilityService.findById(99L)
        );

        // Then
        assertEquals("Disponibilidad no encontrada", exception.getMessage());

        verify(availabilityRepository, times(1)).findById(99L);
    }

    @Test
    void findByRoomId_shouldReturnAvailabilityList() {
        // Given
        when(availabilityRepository.findByRoomId(1L)).thenReturn(List.of(
                buildAvailability(1L, LocalDate.of(2026, 7, 10), "AVAILABLE"),
                buildAvailability(2L, LocalDate.of(2026, 7, 11), "BOOKED")
        ));

        // When
        List<AvailabilityResponseDTO> response = availabilityService.findByRoomId(1L);

        // Then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getRoomId());

        verify(availabilityRepository, times(1)).findByRoomId(1L);
    }

    @Test
    void findByRoomAndDate_shouldReturnAvailability_whenExists() {
        // Given
        LocalDate date = LocalDate.of(2026, 7, 10);
        RoomAvailability availability = buildAvailability(1L, date, "AVAILABLE");

        when(availabilityRepository.findByRoomIdAndAvailableDate(1L, date))
                .thenReturn(Optional.of(availability));

        // When
        AvailabilityResponseDTO response = availabilityService.findByRoomAndDate(1L, date);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getRoomId());
        assertEquals(date, response.getAvailableDate());
        assertEquals("AVAILABLE", response.getStatus());

        verify(availabilityRepository, times(1))
                .findByRoomIdAndAvailableDate(1L, date);
    }

    @Test
    void findByRoomAndDate_shouldThrowException_whenAvailabilityNotFound() {
        // Given
        LocalDate date = LocalDate.of(2026, 7, 10);

        when(availabilityRepository.findByRoomIdAndAvailableDate(1L, date))
                .thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> availabilityService.findByRoomAndDate(1L, date)
        );

        // Then
        assertEquals("No existe disponibilidad para esa habitación y fecha", exception.getMessage());

        verify(availabilityRepository, times(1))
                .findByRoomIdAndAvailableDate(1L, date);
    }

    @Test
    void create_shouldCreateAvailability_whenDateDoesNotExist() {
        // Given
        AvailabilityRequestDTO request = buildAvailabilityRequest("AVAILABLE");

        when(availabilityRepository.existsByRoomIdAndAvailableDate(1L, LocalDate.of(2026, 7, 10)))
                .thenReturn(false);

        when(availabilityRepository.save(any(RoomAvailability.class))).thenAnswer(invocation -> {
            RoomAvailability availability = invocation.getArgument(0);
            availability.setId(1L);
            return availability;
        });

        // When
        AvailabilityResponseDTO response = availabilityService.create(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getRoomId());
        assertEquals(LocalDate.of(2026, 7, 10), response.getAvailableDate());
        assertEquals("AVAILABLE", response.getStatus());
        assertNotNull(response.getCreatedAt());

        verify(availabilityRepository, times(1))
                .existsByRoomIdAndAvailableDate(1L, LocalDate.of(2026, 7, 10));

        verify(availabilityRepository, times(1)).save(any(RoomAvailability.class));
    }

    @Test
    void create_shouldThrowException_whenAvailabilityAlreadyExists() {
        // Given
        AvailabilityRequestDTO request = buildAvailabilityRequest("AVAILABLE");

        when(availabilityRepository.existsByRoomIdAndAvailableDate(1L, LocalDate.of(2026, 7, 10)))
                .thenReturn(true);

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> availabilityService.create(request)
        );

        // Then
        assertEquals("Ya existe disponibilidad para esa habitación en esa fecha", exception.getMessage());

        verify(availabilityRepository, times(1))
                .existsByRoomIdAndAvailableDate(1L, LocalDate.of(2026, 7, 10));

        verify(availabilityRepository, never()).save(any(RoomAvailability.class));
    }

    @Test
    void update_shouldUpdateAvailability_whenExists() {
        // Given
        AvailabilityRequestDTO request = buildAvailabilityRequest("BOOKED");

        RoomAvailability availability = buildAvailability(1L, LocalDate.of(2026, 7, 10), "AVAILABLE");

        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(availability));

        when(availabilityRepository.save(any(RoomAvailability.class))).thenAnswer(invocation -> {
            RoomAvailability updatedAvailability = invocation.getArgument(0);
            return updatedAvailability;
        });

        // When
        AvailabilityResponseDTO response = availabilityService.update(1L, request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getRoomId());
        assertEquals(LocalDate.of(2026, 7, 10), response.getAvailableDate());
        assertEquals("BOOKED", response.getStatus());

        verify(availabilityRepository, times(1)).findById(1L);
        verify(availabilityRepository, times(1)).save(any(RoomAvailability.class));
    }

    @Test
    void update_shouldThrowException_whenAvailabilityNotFound() {
        // Given
        AvailabilityRequestDTO request = buildAvailabilityRequest("BOOKED");

        when(availabilityRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> availabilityService.update(99L, request)
        );

        // Then
        assertEquals("Disponibilidad no encontrada", exception.getMessage());

        verify(availabilityRepository, times(1)).findById(99L);
        verify(availabilityRepository, never()).save(any(RoomAvailability.class));
    }

    @Test
    void checkAvailability_shouldReturnAvailableTrue_whenAllDatesAreAvailable() {
        // Given
        AvailabilityCheckRequestDTO request = buildCheckRequest(
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 12)
        );

        RoomAvailability dayOne = buildAvailability(1L, LocalDate.of(2026, 7, 10), "AVAILABLE");
        RoomAvailability dayTwo = buildAvailability(2L, LocalDate.of(2026, 7, 11), "AVAILABLE");

        when(availabilityRepository.findByRoomIdAndAvailableDate(1L, LocalDate.of(2026, 7, 10)))
                .thenReturn(Optional.of(dayOne));

        when(availabilityRepository.findByRoomIdAndAvailableDate(1L, LocalDate.of(2026, 7, 11)))
                .thenReturn(Optional.of(dayTwo));

        // When
        AvailabilityCheckResponseDTO response = availabilityService.checkAvailability(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getRoomId());
        assertTrue(response.getAvailable());
        assertEquals("La habitación está disponible para el rango solicitado", response.getMessage());

        verify(availabilityRepository, times(2)).findByRoomIdAndAvailableDate(any(), any());
    }

    @Test
    void checkAvailability_shouldReturnAvailableFalse_whenOneDateIsNotAvailable() {
        // Given
        AvailabilityCheckRequestDTO request = buildCheckRequest(
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 12)
        );

        RoomAvailability dayOne = buildAvailability(1L, LocalDate.of(2026, 7, 10), "AVAILABLE");
        RoomAvailability dayTwo = buildAvailability(2L, LocalDate.of(2026, 7, 11), "BOOKED");

        when(availabilityRepository.findByRoomIdAndAvailableDate(1L, LocalDate.of(2026, 7, 10)))
                .thenReturn(Optional.of(dayOne));

        when(availabilityRepository.findByRoomIdAndAvailableDate(1L, LocalDate.of(2026, 7, 11)))
                .thenReturn(Optional.of(dayTwo));

        // When
        AvailabilityCheckResponseDTO response = availabilityService.checkAvailability(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getRoomId());
        assertFalse(response.getAvailable());
        assertEquals("La habitación no está disponible en la fecha: 2026-07-11", response.getMessage());

        verify(availabilityRepository, times(2)).findByRoomIdAndAvailableDate(any(), any());
    }

    @Test
    void checkAvailability_shouldThrowException_whenCheckOutDateIsBeforeCheckInDate() {
        // Given
        AvailabilityCheckRequestDTO request = buildCheckRequest(
                LocalDate.of(2026, 7, 12),
                LocalDate.of(2026, 7, 10)
        );

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> availabilityService.checkAvailability(request)
        );

        // Then
        assertEquals("La fecha de salida debe ser posterior a la fecha de entrada", exception.getMessage());

        verifyNoInteractions(availabilityRepository);
    }

    @Test
    void checkAvailability_shouldThrowException_whenDateDoesNotExist() {
        // Given
        AvailabilityCheckRequestDTO request = buildCheckRequest(
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 11)
        );

        when(availabilityRepository.findByRoomIdAndAvailableDate(1L, LocalDate.of(2026, 7, 10)))
                .thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> availabilityService.checkAvailability(request)
        );

        // Then
        assertEquals("No existe disponibilidad para la fecha: 2026-07-10", exception.getMessage());

        verify(availabilityRepository, times(1))
                .findByRoomIdAndAvailableDate(1L, LocalDate.of(2026, 7, 10));
    }

    @Test
    void changeStatus_shouldChangeAvailabilityStatus_whenExists() {
        // Given
        RoomAvailability availability = buildAvailability(1L, LocalDate.of(2026, 7, 10), "AVAILABLE");

        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(availability));

        when(availabilityRepository.save(any(RoomAvailability.class))).thenAnswer(invocation -> {
            RoomAvailability updatedAvailability = invocation.getArgument(0);
            return updatedAvailability;
        });

        // When
        AvailabilityResponseDTO response = availabilityService.changeStatus(1L, "BOOKED");

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("BOOKED", response.getStatus());

        verify(availabilityRepository, times(1)).findById(1L);
        verify(availabilityRepository, times(1)).save(any(RoomAvailability.class));
    }

    @Test
    void changeStatus_shouldThrowException_whenAvailabilityNotFound() {
        // Given
        when(availabilityRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> availabilityService.changeStatus(99L, "BOOKED")
        );

        // Then
        assertEquals("Disponibilidad no encontrada", exception.getMessage());

        verify(availabilityRepository, times(1)).findById(99L);
        verify(availabilityRepository, never()).save(any(RoomAvailability.class));
    }

    private AvailabilityRequestDTO buildAvailabilityRequest(String status) {
        AvailabilityRequestDTO request = new AvailabilityRequestDTO();
        request.setRoomId(1L);
        request.setAvailableDate(LocalDate.of(2026, 7, 10));
        request.setStatus(status);
        return request;
    }

    private AvailabilityCheckRequestDTO buildCheckRequest(LocalDate checkInDate, LocalDate checkOutDate) {
        AvailabilityCheckRequestDTO request = new AvailabilityCheckRequestDTO();
        request.setRoomId(1L);
        request.setCheckInDate(checkInDate);
        request.setCheckOutDate(checkOutDate);
        return request;
    }

    private RoomAvailability buildAvailability(Long id, LocalDate date, String status) {
        RoomAvailability availability = new RoomAvailability();
        availability.setId(id);
        availability.setRoomId(1L);
        availability.setAvailableDate(date);
        availability.setStatus(status);
        availability.setCreatedAt(LocalDateTime.now());
        return availability;
    }
}