package com.duoc.reservams.availabilityservice.service;

import com.duoc.reservams.availabilityservice.dto.*;
import com.duoc.reservams.availabilityservice.model.RoomAvailability;
import com.duoc.reservams.availabilityservice.repository.RoomAvailabilityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// aqui va la lógica de negocio de disponibilidad
@Service
public class AvailabilityService {

    private final RoomAvailabilityRepository availabilityRepository;

    public AvailabilityService(RoomAvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    public List<AvailabilityResponseDTO> findAll() {
        return availabilityRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public AvailabilityResponseDTO findById(Long id) {
        RoomAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilidad no encontrada"));

        return toResponseDTO(availability);
    }

    public List<AvailabilityResponseDTO> findByRoomId(Long roomId) {
        return availabilityRepository.findByRoomId(roomId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public AvailabilityResponseDTO findByRoomAndDate(Long roomId, LocalDate date) {
        RoomAvailability availability = availabilityRepository.findByRoomIdAndAvailableDate(roomId, date)
                .orElseThrow(() -> new RuntimeException("No existe disponibilidad para esa habitación y fecha"));

        return toResponseDTO(availability);
    }

    public AvailabilityResponseDTO create(AvailabilityRequestDTO request) {
        boolean exists = availabilityRepository
                .existsByRoomIdAndAvailableDate(request.getRoomId(), request.getAvailableDate());

        if (exists) {
            throw new RuntimeException("Ya existe disponibilidad para esa habitación en esa fecha");
        }

        RoomAvailability availability = new RoomAvailability();
        availability.setRoomId(request.getRoomId());
        availability.setAvailableDate(request.getAvailableDate());
        availability.setStatus(request.getStatus());
        availability.setCreatedAt(LocalDateTime.now());

        RoomAvailability savedAvailability = availabilityRepository.save(availability);

        return toResponseDTO(savedAvailability);
    }

    public AvailabilityResponseDTO update(Long id, AvailabilityRequestDTO request) {
        RoomAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilidad no encontrada"));

        availability.setRoomId(request.getRoomId());
        availability.setAvailableDate(request.getAvailableDate());
        availability.setStatus(request.getStatus());

        RoomAvailability updatedAvailability = availabilityRepository.save(availability);

        return toResponseDTO(updatedAvailability);
    }

    public AvailabilityCheckResponseDTO checkAvailability(AvailabilityCheckRequestDTO request) {
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new RuntimeException("La fecha de salida debe ser posterior a la fecha de entrada");
        }

        LocalDate currentDate = request.getCheckInDate();

        // revisamos día por dia hasta antes del checkOut
        while (currentDate.isBefore(request.getCheckOutDate())) {
            LocalDate finalCurrentDate = currentDate;
            RoomAvailability availability = availabilityRepository
                    .findByRoomIdAndAvailableDate(request.getRoomId(), currentDate)
                    .orElseThrow(() -> new RuntimeException("No existe disponibilidad para la fecha: " + finalCurrentDate));

            if (!availability.getStatus().equals("AVAILABLE")) {
                return new AvailabilityCheckResponseDTO(
                        request.getRoomId(),
                        false,
                        "La habitación no está disponible en la fecha: " + currentDate
                );
            }

            currentDate = currentDate.plusDays(1);
        }

        return new AvailabilityCheckResponseDTO(
                request.getRoomId(),
                true,
                "La habitación está disponible para el rango solicitado"
        );
    }

    public AvailabilityResponseDTO changeStatus(Long id, String status) {
        RoomAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilidad no encontrada"));

        availability.setStatus(status);

        RoomAvailability updatedAvailability = availabilityRepository.save(availability);

        return toResponseDTO(updatedAvailability);
    }

    // convierte la entidad a DTO de respuesta
    private AvailabilityResponseDTO toResponseDTO(RoomAvailability availability) {
        return new AvailabilityResponseDTO(
                availability.getId(),
                availability.getRoomId(),
                availability.getAvailableDate(),
                availability.getStatus(),
                availability.getCreatedAt()
        );
    }
}