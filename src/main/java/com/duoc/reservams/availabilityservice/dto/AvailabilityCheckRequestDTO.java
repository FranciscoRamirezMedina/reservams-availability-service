package com.duoc.reservams.availabilityservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

// DTO para consultar si una habitación esta disponible en un rango de fechas
@Data
public class AvailabilityCheckRequestDTO {

    @NotNull(message = "El roomId es obligatorio")
    private Long roomId;

    @NotNull(message = "La fecha de entrada es obligatoria")
    private LocalDate checkInDate;

    @NotNull(message = "La fecha de salida es obligatoria")
    private LocalDate checkOutDate;
}