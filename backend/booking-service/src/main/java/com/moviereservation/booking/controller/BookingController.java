package com.moviereservation.booking.controller;

import com.moviereservation.booking.dto.request.BookingRequest;
import com.moviereservation.booking.dto.response.BookingResponse;
import com.moviereservation.booking.dto.response.SeatResponse;
import com.moviereservation.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }

    @GetMapping("/users/{userId}")
    public List<BookingResponse> getBookingsByUserId(@PathVariable String userId) {
        return bookingService.getBookingsByUserId(userId);
    }

    @GetMapping("/showtimes/{showtimeId}/seats")
    public List<SeatResponse> getSeats(@PathVariable String showtimeId) {
        return bookingService.getSeats(showtimeId);
    }

    @DeleteMapping("/{bookingId}")
    public BookingResponse cancelBooking(@PathVariable String bookingId, @RequestParam String userId) {
        return bookingService.cancelBooking(bookingId, userId);
    }
}