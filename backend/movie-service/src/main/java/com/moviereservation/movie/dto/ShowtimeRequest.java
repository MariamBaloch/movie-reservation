package com.moviereservation.movie.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShowtimeRequest {

    @NotNull
    private String movieId;

    @NotNull
    private String screenId;

    @NotNull
    @Future
    private LocalDateTime startsAt;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;
}
