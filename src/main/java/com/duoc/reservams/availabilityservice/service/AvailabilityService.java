package com.duoc.reservams.availabilityservice.service;

import com.duoc.reservams.availabilityservice.dto.*;
import com.duoc.reservams.availabilityservice.model.RoomAvailability;
import com.duoc.reservams.availabilityservice.repository.RoomAvailabilityRepository;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// aqui va la lógica de negocio de disponibilidad
@Service
public class AvailabilityService {

    private static final Logger logger = LoggerFactory.getLogger(AvailabilityService.class);

    private final RoomAvailabilityRepository availabilityRepository;

    public AvailabilityService(RoomAvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    public List<AvailabilityResponseDTO> findAll() {
        logger.info("Listando todos los registros de disponibilidad");

        List<AvailabilityResponseDTO> availabilityList = availabilityRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();

        logger.info("Se encontraron {} registros de disponibilidad", availabilityList.size());

        return availabilityList;
    }

    public AvailabilityResponseDTO findById(Long id) {
        logger.info("Buscando disponibilidad por ID {}", id);

        RoomAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se encontro disponibilidad con ID {}", id);
                    return new RuntimeException("Disponibilidad no encontrada");
                });

        logger.info("Disponibilidad encontrada con ID {}, habitacion ID {} y fecha {}",
                availability.getId(),
                availability.getRoomId(),
                availability.getAvailableDate());

        return toResponseDTO(availability);
    }

    public List<AvailabilityResponseDTO> findByRoomId(Long roomId) {
        logger.info("Listando disponibilidad para habitacion ID {}", roomId);

        List<AvailabilityResponseDTO> availabilityList = availabilityRepository.findByRoomId(roomId)
                .stream()
                .map(this::toResponseDTO)
                .toList();

        logger.info("Se encontraron {} registros de disponibilidad para habitacion ID {}",
                availabilityList.size(),
                roomId);

        return availabilityList;
    }

    public AvailabilityResponseDTO findByRoomAndDate(Long roomId, LocalDate date) {
        logger.info("Buscando disponibilidad para habitacion ID {} en fecha {}", roomId, date);

        RoomAvailability availability = availabilityRepository.findByRoomIdAndAvailableDate(roomId, date)
                .orElseThrow(() -> {
                    logger.warn("No existe disponibilidad para habitacion ID {} en fecha {}", roomId, date);
                    return new RuntimeException("No existe disponibilidad para esa habitación y fecha");
                });

        logger.info("Disponibilidad encontrada para habitacion ID {} en fecha {} con estado {}",
                roomId,
                date,
                availability.getStatus());

        return toResponseDTO(availability);
    }

    public AvailabilityResponseDTO create(AvailabilityRequestDTO request) {
        logger.info("Iniciando creacion de disponibilidad para habitacion ID {} en fecha {}",
                request.getRoomId(),
                request.getAvailableDate());

        boolean exists = availabilityRepository
                .existsByRoomIdAndAvailableDate(request.getRoomId(), request.getAvailableDate());

        if (exists) {
            logger.warn("No se pudo crear disponibilidad. Ya existe registro para habitacion ID {} en fecha {}",
                    request.getRoomId(),
                    request.getAvailableDate());
            throw new RuntimeException("Ya existe disponibilidad para esa habitación en esa fecha");
        }

        RoomAvailability availability = new RoomAvailability();
        availability.setRoomId(request.getRoomId());
        availability.setAvailableDate(request.getAvailableDate());
        availability.setStatus(request.getStatus());
        availability.setCreatedAt(LocalDateTime.now());

        logger.info("Guardando disponibilidad para habitacion ID {} en fecha {} con estado {}",
                availability.getRoomId(),
                availability.getAvailableDate(),
                availability.getStatus());

        RoomAvailability savedAvailability = availabilityRepository.save(availability);

        logger.info("Disponibilidad creada correctamente con ID {}, habitacion ID {} y fecha {}",
                savedAvailability.getId(),
                savedAvailability.getRoomId(),
                savedAvailability.getAvailableDate());

        return toResponseDTO(savedAvailability);
    }

    public AvailabilityResponseDTO update(Long id, AvailabilityRequestDTO request) {
        logger.info("Iniciando actualizacion de disponibilidad con ID {}", id);

        RoomAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se pudo actualizar. Disponibilidad no encontrada con ID {}", id);
                    return new RuntimeException("Disponibilidad no encontrada");
                });

        availability.setRoomId(request.getRoomId());
        availability.setAvailableDate(request.getAvailableDate());
        availability.setStatus(request.getStatus());

        logger.info("Guardando cambios de disponibilidad ID {} con estado {}",
                id,
                availability.getStatus());

        RoomAvailability updatedAvailability = availabilityRepository.save(availability);

        logger.info("Disponibilidad actualizada correctamente con ID {}, habitacion ID {} y fecha {}",
                updatedAvailability.getId(),
                updatedAvailability.getRoomId(),
                updatedAvailability.getAvailableDate());

        return toResponseDTO(updatedAvailability);
    }

    public AvailabilityCheckResponseDTO checkAvailability(AvailabilityCheckRequestDTO request) {
        logger.info("Iniciando validacion de disponibilidad para habitacion ID {} desde {} hasta {}",
                request.getRoomId(),
                request.getCheckInDate(),
                request.getCheckOutDate());

        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            logger.warn("Rango de fechas invalido para habitacion ID {}. Check-in: {}, Check-out: {}",
                    request.getRoomId(),
                    request.getCheckInDate(),
                    request.getCheckOutDate());
            throw new RuntimeException("La fecha de salida debe ser posterior a la fecha de entrada");
        }

        LocalDate currentDate = request.getCheckInDate();

        // revisamos día por dia hasta antes del checkOut
        while (currentDate.isBefore(request.getCheckOutDate())) {
            logger.info("Revisando disponibilidad de habitacion ID {} en fecha {}",
                    request.getRoomId(),
                    currentDate);

            LocalDate finalCurrentDate = currentDate;
            RoomAvailability availability = availabilityRepository
                    .findByRoomIdAndAvailableDate(request.getRoomId(), currentDate)
                    .orElseThrow(() -> {
                        logger.warn("No existe disponibilidad para la habitacion ID {} en fecha {}",
                                request.getRoomId(),
                                finalCurrentDate);
                        return new RuntimeException("No existe disponibilidad para la fecha: " + finalCurrentDate);
                    });

            if (!availability.getStatus().equals("AVAILABLE")) {
                logger.warn("Habitacion ID {} no disponible en fecha {}. Estado actual: {}",
                        request.getRoomId(),
                        currentDate,
                        availability.getStatus());

                return new AvailabilityCheckResponseDTO(
                        request.getRoomId(),
                        false,
                        "La habitación no está disponible en la fecha: " + currentDate
                );
            }

            currentDate = currentDate.plusDays(1);
        }

        logger.info("Habitacion ID {} disponible para el rango solicitado desde {} hasta {}",
                request.getRoomId(),
                request.getCheckInDate(),
                request.getCheckOutDate());

        return new AvailabilityCheckResponseDTO(
                request.getRoomId(),
                true,
                "La habitación está disponible para el rango solicitado"
        );
    }

    public AvailabilityResponseDTO changeStatus(Long id, String status) {
        logger.info("Iniciando cambio de estado de disponibilidad ID {} a {}", id, status);

        RoomAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se pudo cambiar estado. Disponibilidad no encontrada con ID {}", id);
                    return new RuntimeException("Disponibilidad no encontrada");
                });

        availability.setStatus(status);

        RoomAvailability updatedAvailability = availabilityRepository.save(availability);

        logger.info("Estado de disponibilidad ID {} cambiado correctamente a {}",
                updatedAvailability.getId(),
                updatedAvailability.getStatus());

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