package com.moviereservation.booking.service;

import com.moviereservation.booking.model.Seat;
import com.moviereservation.booking.dto.request.SeatInitializationRequest;
import com.moviereservation.booking.dto.response.SeatResponse;
import com.moviereservation.booking.enums.SeatStatus;
import com.moviereservation.booking.mapper.SeatMapper;
import com.moviereservation.booking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatInventoryService {

    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;

    @Transactional
    public List<SeatResponse> initializeSeats(String showtimeId, SeatInitializationRequest request) {

        List<String> seatNumbers = request.getSeatNumbers().stream()
                .map(String::trim).map(String::toUpperCase).toList();

        Set<String> requestedSeats = new HashSet<>(seatNumbers);

        if (requestedSeats.size() != seatNumbers.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate seat numbers are not allowed");
        }

        List<Seat> existingSeats = seatRepository.findAllByShowtimeIdOrderBySeatNumberAsc(showtimeId);

        if (!existingSeats.isEmpty()) {
            Set<String> existingSeatNumbers = existingSeats.stream()
                    .map(Seat::getSeatNumber).collect(Collectors.toSet());

            if (existingSeatNumbers.equals(requestedSeats)) {
                return existingSeats.stream().map(seatMapper::toResponse).toList();
            }

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat inventory has already been initialized");
        }

        List<Seat> seats = seatNumbers.stream()
                .map(number -> Seat.builder()
                        .showtimeId(showtimeId)
                        .seatNumber(number)
                        .status(SeatStatus.AVAILABLE)
                        .build())
                .toList();

        return seatRepository.saveAll(seats).stream().map(seatMapper::toResponse).toList();
    }
}