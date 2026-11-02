package com.moviereservation.booking.service;

import com.moviereservation.booking.client.MovieServiceClient;
import com.moviereservation.booking.enums.ShowtimeStatus;
import com.moviereservation.booking.model.Booking;
import com.moviereservation.booking.dto.client.ShowtimeResponse;
import com.moviereservation.booking.dto.request.BookingRequest;
import com.moviereservation.booking.dto.response.BookingResponse;
import com.moviereservation.booking.dto.response.SeatResponse;
import com.moviereservation.booking.enums.BookingStatus;
import com.moviereservation.booking.enums.SeatStatus;
import com.moviereservation.booking.mapper.BookingMapper;
import com.moviereservation.booking.mapper.SeatMapper;
import com.moviereservation.booking.repository.BookingRepository;
import com.moviereservation.booking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final BookingMapper bookingMapper;
    private final SeatMapper seatMapper;
    private final MovieServiceClient movieServiceClient;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {

        List<String> seatNumbers = request.getSeatNumbers().stream().map(String::trim).map(String::toUpperCase).toList();

        if (new HashSet<>(seatNumbers).size() != seatNumbers.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate seat numbers are not allowed");
        }

        ShowtimeResponse showtime = movieServiceClient.getShowtime(request.getShowtimeId());
        validateShowtime(showtime, request.getShowtimeId());

        BigDecimal totalAmount = showtime.price().multiply(BigDecimal.valueOf(seatNumbers.size()));

        Booking booking = Booking.builder()
                .userId(request.getUserId())
                .showtimeId(showtime.id())
                .seatNumbers(seatNumbers)
                .totalAmount(totalAmount)
                .status(BookingStatus.CONFIRMED)
                .createdAt(Instant.now())
                .build();

        booking = bookingRepository.save(booking);

        for (String seatNumber : seatNumbers) {
            reserveSeat(showtime.id(), seatNumber, booking.getId());
        }

        return bookingMapper.toResponse(booking);
    }

    private void reserveSeat(String showtimeId, String seatNumber, String bookingId) {
        long updated = seatRepository.reserveSeat(showtimeId, seatNumber, SeatStatus.AVAILABLE, SeatStatus.BOOKED, bookingId);

        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat " + seatNumber + " is unavailable or does not exist");
        }
    }

    public List<BookingResponse> getBookingsByUserId(String userId) {

        return bookingRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    public List<SeatResponse> getSeats(String showtimeId) {

        ShowtimeResponse showtime = movieServiceClient.getShowtime(showtimeId);
        validateShowtime(showtime, showtimeId);

        return seatRepository.findAllByShowtimeIdOrderBySeatNumberAsc(showtimeId)
                .stream()
                .map(seatMapper::toResponse)
                .toList();
    }

    @Transactional
    public BookingResponse cancelBooking(String bookingId, String userId) {

        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is already cancelled");
        }

        ShowtimeResponse showtime = movieServiceClient.getShowtime(booking.getShowtimeId());

        if (showtime.startsAt() == null || !showtime.startsAt().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel a booking after the showtime has started");
        }

        long cancelled = bookingRepository.cancelConfirmedBooking(bookingId, userId);

        if (cancelled == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is already cancelled");
        }

        long releasedSeats = seatRepository.releaseBookedSeats(booking.getShowtimeId(), booking.getId());

        if (releasedSeats != booking.getSeatNumbers().size()) {
            throw new IllegalStateException("Could not release all reserved seats");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        return bookingMapper.toResponse(booking);
    }

    private void validateShowtime(ShowtimeResponse showtime, String requestedId) {

        if (showtime.status() != ShowtimeStatus.SCHEDULED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Showtime is not available for booking");
        }

        if ( !showtime.startsAt().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Showtime has already started");
        }

        if (showtime.price().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Invalid ticket price from Movie Service");
        }
    }
}