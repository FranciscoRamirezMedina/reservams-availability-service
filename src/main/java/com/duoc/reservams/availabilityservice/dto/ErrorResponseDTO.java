package com.duoc.reservams.availabilityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

// DTO simple para responder errores
@Data
@AllArgsConstructor
public class ErrorResponseDTO {

    private String message;
    private int status;
    private LocalDateTime timestamp;
}