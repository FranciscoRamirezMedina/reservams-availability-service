package com.duoc.reservams.availabilityservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// esta clase representa la disponibilidad de una habitación en una fecha
@Entity
@Table(name = "room_availability")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailability {

    // ID principal del registro de disponibilidad
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID logico de la habitacion que viene desde room-service
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    // fecha especifica que se esta controlando
    @Column(name = "available_date", nullable = false)
    private LocalDate availableDate;

    // estado de la habitacion en esa fecha, AVAILABLE, RESERVED o BLOCKED
    @Column(nullable = false, length = 30)
    private String status;

    // fecha en que se creo este registro
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}