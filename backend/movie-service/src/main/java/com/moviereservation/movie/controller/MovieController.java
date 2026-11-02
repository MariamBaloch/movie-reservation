package com.moviereservation.movie.controller;

import com.moviereservation.movie.dto.MovieRequest;
import com.moviereservation.movie.model.Movie;
import com.moviereservation.movie.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @GetMapping
    public List<Movie> findAll(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Movie findById(@PathVariable String id){
        return service.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody MovieRequest request){
        Movie movie = service.createMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(movie);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public Movie updateMovie(@PathVariable String id, @RequestBody MovieRequest request){
        return service.updateMovie(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> deleteMovie(@PathVariable String id){
        service.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
