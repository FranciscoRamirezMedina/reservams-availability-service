package com.duoc.reservams.availabilityservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

// DTO para crear o actualizar disponibilidad
@Data
public class AvailabilityRequestDTO {

    @NotNull(message = "El roomId es obligatorio")
    private Long roomId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate availableDate;

    @NotBlank(message = "El estado es obligatorio")
    private String status;
}