package com.moviereservation.movie.dto;

import java.util.List;

public record SeatInitializationRequest(
        List<String> seatNumbers
) {
}
