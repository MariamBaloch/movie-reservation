package com.moviereservation.booking.controller;

import com.moviereservation.booking.dto.request.BookingRequest;
import com.moviereservation.booking.dto.response.BookingResponse;
import com.moviereservation.booking.dto.response.SeatResponse;
import com.moviereservation.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody BookingRequest request,
                                         @AuthenticationPrincipal Jwt jwt) {
        return bookingService.createBooking(request, jwt.getSubject());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public List<BookingResponse> getMyBookings(@AuthenticationPrincipal Jwt jwt) {
        return bookingService.getBookingsByUserId(jwt.getSubject());
    }

    @GetMapping("/showtimes/{showtimeId}/seats")
    @PreAuthorize("hasRole('USER')")
    public List<SeatResponse> getSeats(@PathVariable String showtimeId) {
        return bookingService.getSeats(showtimeId);
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasRole('USER')")
    public BookingResponse cancelBooking(@PathVariable String bookingId,
                                         @AuthenticationPrincipal Jwt jwt) {
        return bookingService.cancelBooking(bookingId, jwt.getSubject());
    }
}
