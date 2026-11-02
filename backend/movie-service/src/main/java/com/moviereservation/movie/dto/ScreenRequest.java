package com.moviereservation.movie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScreenRequest(
        @NotBlank String name,
        @NotNull Integer rows,
        @NotNull Integer seatsPerRow
) {
}
