package com.moviereservation.booking.dto.client;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ShowtimeResponse(
        String id,
        LocalDateTime startsAt,
        BigDecimal price,
        String status
) {}