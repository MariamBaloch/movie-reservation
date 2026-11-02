package com.moviereservation.booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    // TODO remove once auth service is implemented and userId can be extracted from the JWT token
    @NotBlank
    private String userId;

    @NotBlank
    private String showtimeId;

    @NotEmpty
    private List<@NotBlank String> seatNumbers;
}