package com.moviereservation.booking.client;

import com.moviereservation.booking.dto.client.ShowtimeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class MovieServiceClient {

    private final RestClient restClient;

    public MovieServiceClient(RestClient.Builder builder, @Value("${services.movie.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public ShowtimeResponse getShowtime(String showtimeId) {
        return restClient.get()
                .uri("/api/showtimes/{id}", showtimeId)
                .retrieve()
                .body(ShowtimeResponse.class);
    }
}