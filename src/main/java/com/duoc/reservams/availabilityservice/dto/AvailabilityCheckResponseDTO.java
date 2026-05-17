package com.duoc.reservams.availabilityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO para responder si una habitacion esta disponible o no
@Data
@AllArgsConstructor
public class AvailabilityCheckResponseDTO {

    private Long roomId;
    private Boolean available;
    private String message;
}