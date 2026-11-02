package com.moviereservation.movie.client;

import com.moviereservation.movie.dto.SeatInitializationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingServiceClient {

    private final RestClient restClient;

    public void initializeSeats(String showtimeId, List<String> seatNumbers){
        SeatInitializationRequest request = new SeatInitializationRequest(seatNumbers);

        restClient.post().uri("/api/showtimes/{showtimeId}/seats", showtimeId).body(request).retrieve().toBodilessEntity();
    }
}
