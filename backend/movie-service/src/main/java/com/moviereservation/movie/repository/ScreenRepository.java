package com.moviereservation.movie.repository;

import com.moviereservation.movie.model.Screen;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ScreenRepository extends MongoRepository<Screen,String> {
    List<Screen> findByActiveTrue();
    Optional<Screen> findByIdAndActiveTrue(String id);
}
