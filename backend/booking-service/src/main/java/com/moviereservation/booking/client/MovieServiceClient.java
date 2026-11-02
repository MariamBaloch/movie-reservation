package com.moviereservation.booking.client;

import com.moviereservation.booking.dto.client.ShowtimeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import jakarta.validation.Validator;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j

public class MovieServiceClient {

    private final RestClient restClient;
    private final Validator validator;

    public MovieServiceClient(RestClient.Builder builder, Validator validator,
                              @Value("${services.movie.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.validator = validator;
    }

    public ShowtimeResponse getShowtime(String showtimeId) {
        ShowtimeResponse response = restClient.get()
                .uri("/api/showtimes/{id}", showtimeId)
                .retrieve()
                .body(ShowtimeResponse.class);

        if (response == null || !validator.validate(response).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Invalid response from Movie Service");
        }

        if (!response.id().equals(showtimeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unexpected showtime ID");
        }

        return response;
    }
}