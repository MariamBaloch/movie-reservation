package com.moviereservation.booking.controller;

import com.moviereservation.booking.dto.request.SeatInitializationRequest;
import com.moviereservation.booking.dto.response.SeatResponse;
import com.moviereservation.booking.service.SeatInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class SeatInventoryController {

    private final SeatInventoryService seatInventoryService;

    @PostMapping("/{showtimeId}/seats")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public List<SeatResponse> initializeSeats(@PathVariable String showtimeId, @Valid @RequestBody SeatInitializationRequest request) {
        return seatInventoryService.initializeSeats(showtimeId, request);
    }
}
