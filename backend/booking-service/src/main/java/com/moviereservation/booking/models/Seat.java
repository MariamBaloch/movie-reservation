package com.moviereservation.booking.models;

import com.moviereservation.booking.enums.SeatStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "seats")
@CompoundIndex(
        name = "showtime_seat_unique",
        def = "{'showtimeId': 1, 'seatNumber': 1}",
        unique = true
)
public class Seat {

    @Id
    private String id;

    private String showtimeId;

    private String seatNumber;

    private SeatStatus status;

    private String bookingId;
}