package com.moviereservation.movie.controller;

import com.moviereservation.movie.dto.ShowtimeRequest;
import com.moviereservation.movie.dto.ShowtimeResponse;
import com.moviereservation.movie.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping
    //@PreAuthorize("hasRole('Admin')")
    @ResponseStatus(HttpStatus.CREATED)
    public ShowtimeResponse createShowtime(@Valid @RequestBody ShowtimeRequest request) {
        return showtimeService.createShowtime(request);
    }

    @GetMapping
    public List<ShowtimeResponse> getAll() {
        return showtimeService.getAll();
    }

    @GetMapping("{id}")
    public ShowtimeResponse getById(@PathVariable String id) {
        return showtimeService.getById(id);
    }

    @GetMapping("movie/{movieId}")
    public List<ShowtimeResponse> getByMovieId(@PathVariable String movieId) {
        return showtimeService.getByMovieId(movieId);
    }

    @PutMapping("{id}")
    public ShowtimeResponse updateShowtime(@PathVariable String id,@Valid @RequestBody ShowtimeRequest request) {
        return showtimeService.updateShowtime(id, request);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShowtime(@PathVariable String id) {
        showtimeService.deleteShowtime(id);
    }
}
