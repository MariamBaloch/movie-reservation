package com.moviereservation.booking.model;

import com.moviereservation.booking.enums.BookingStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bookings")
@CompoundIndex(
        name = "active_booking_seat_unique",
        def = "{'showtimeId': 1, 'seatNumbers': 1}",
        unique = true,
        partialFilter = "{'status': 'CONFIRMED'}"
)
public class Booking {

    @Id
    private String id;

    private String userId;

    private String showtimeId;

    private List<String> seatNumbers;

    private BigDecimal totalAmount;

    private BookingStatus status;

    private Instant createdAt;
}
