package com.moviereservation.movie.client;

import com.moviereservation.movie.dto.SeatInitializationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingServiceClient {

    private final RestClient restClient;

    public void initializeSeats(String showtimeId, List<String> seatNumbers){
        SeatInitializationRequest request = new SeatInitializationRequest(seatNumbers);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            throw new IllegalStateException("An authenticated admin token is required to initialize seats");
        }

        restClient.post()
                .uri("/api/showtimes/{showtimeId}/seats", showtimeId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtAuthentication.getToken().getTokenValue())
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
