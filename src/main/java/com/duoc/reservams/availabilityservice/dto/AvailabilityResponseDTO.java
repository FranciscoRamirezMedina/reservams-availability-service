package com.duoc.reservams.availabilityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

// DTO para responder datos de disponibilidad
@Data
@AllArgsConstructor
public class AvailabilityResponseDTO {

    private Long id;
    private Long roomId;
    private LocalDate availableDate;
    private String status;
    private LocalDateTime createdAt;
}