package com.yhjun.meetingreservation.controller;

import com.yhjun.meetingreservation.domain.Reservation;
import com.yhjun.meetingreservation.dto.ReservationRequest;
import com.yhjun.meetingreservation.service.ReservationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@Tag(name = "예약 API")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping
    public Reservation reserve(@RequestBody @Valid ReservationRequest request) {
        return service.reserve(request);
    }

    @GetMapping
    public List<Reservation> getAll() {
        return service.getAll();
    }

    @GetMapping("/user/{userId}")
    public List<Reservation> getByUser(@PathVariable Long userId) {
        return service.getByUser(userId);
    }
}