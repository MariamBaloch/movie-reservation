package com.moviereservation.booking.dto.response;

import com.moviereservation.booking.enums.BookingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private String id;

    private String showtimeId;

    private List<String> seatNumbers;

    private BigDecimal totalAmount;

    private BookingStatus status;

    private Instant createdAt;
}