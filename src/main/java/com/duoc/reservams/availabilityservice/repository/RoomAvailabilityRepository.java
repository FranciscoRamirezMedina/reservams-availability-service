package com.duoc.reservams.availabilityservice.repository;

import com.duoc.reservams.availabilityservice.model.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// repository para acceder a la tabla room_availability
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

    // lista disponibilidad de una habitacion
    List<RoomAvailability> findByRoomId(Long roomId);

    // busca disponibilidad de una habitacion en una fecha exacta
    Optional<RoomAvailability> findByRoomIdAndAvailableDate(Long roomId, LocalDate availableDate);

    // verifica si ya existe disponibilidad para esa habitacion y fecha
    boolean existsByRoomIdAndAvailableDate(Long roomId, LocalDate availableDate);

    // lista disponibilidad por estado
    List<RoomAvailability> findByStatus(String status);
}