package com.moviereservation.booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatInitializationRequest {

    @NotEmpty
    private List<@NotBlank String> seatNumbers;
}