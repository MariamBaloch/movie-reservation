package com.moviereservation.movie.dto;

import com.moviereservation.movie.model.ShowtimeStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShowtimeResponse {

    private String id;
    private String movieId;
    private String screenId;
    private ShowtimeStatus status;
    private LocalDateTime startsAt;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
