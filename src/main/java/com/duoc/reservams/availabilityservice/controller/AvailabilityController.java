package com.duoc.reservams.availabilityservice.controller;

import com.duoc.reservams.availabilityservice.dto.*;
import com.duoc.reservams.availabilityservice.service.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// controlador REST para manejar disponibilidad de habitaciones
@RestController
@RequestMapping("/api/v1/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    // lista todos los registros de disponibilidad
    @GetMapping
    public ResponseEntity<List<AvailabilityResponseDTO>> findAll() {
        return ResponseEntity.ok(availabilityService.findAll());
    }

    // busca disponibilidad por ID
    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(availabilityService.findById(id));
    }

    // lista disponibilidad de una habitacion
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<AvailabilityResponseDTO>> findByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(availabilityService.findByRoomId(roomId));
    }

    // busca disponibilidad de una habitacion en una fecha
    @GetMapping("/room/{roomId}/date/{date}")
    public ResponseEntity<AvailabilityResponseDTO> findByRoomAndDate(
            @PathVariable Long roomId,
            @PathVariable LocalDate date) {

        return ResponseEntity.ok(availabilityService.findByRoomAndDate(roomId, date));
    }

    // crea disponibilidad para una habitacion
    @PostMapping
    public ResponseEntity<AvailabilityResponseDTO> create(@Valid @RequestBody AvailabilityRequestDTO request) {
        return ResponseEntity.ok(availabilityService.create(request));
    }

    // actualiza disponibilidad completa
    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody AvailabilityRequestDTO request) {

        return ResponseEntity.ok(availabilityService.update(id, request));
    }

    // verifica si una habitación esta disponible en un rango de fechas
    @PostMapping("/check")
    public ResponseEntity<AvailabilityCheckResponseDTO> checkAvailability(
            @Valid @RequestBody AvailabilityCheckRequestDTO request) {

        return ResponseEntity.ok(availabilityService.checkAvailability(request));
    }

    // cambia el estado de una disponibilidad específica
    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<AvailabilityResponseDTO> changeStatus(
            @PathVariable Long id,
            @PathVariable String status) {

        return ResponseEntity.ok(availabilityService.changeStatus(id, status));
    }
}