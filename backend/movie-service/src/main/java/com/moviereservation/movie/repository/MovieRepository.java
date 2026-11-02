package com.moviereservation.movie.repository;

import com.moviereservation.movie.model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends MongoRepository<Movie,String> {
    List<Movie> findByActiveTrue();
    Optional<Movie> findByIdAndActiveTrue(String id);
}
