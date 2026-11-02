package com.moviereservation.booking.dto.response;

import com.moviereservation.booking.enums.SeatStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {

    private String seatNumber;

    private SeatStatus status;
}