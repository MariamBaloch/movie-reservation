package com.moviereservation.booking.dto.client;

import com.moviereservation.booking.enums.ShowtimeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ShowtimeResponse(
        @NotBlank String id,
        @NotNull LocalDateTime startsAt,
        @NotNull @Positive BigDecimal price,
        @NotNull ShowtimeStatus status
) {}