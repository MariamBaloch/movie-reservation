package com.moviereservation.movie.repository;

import com.moviereservation.movie.model.Showtime;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ShowtimeRepository  extends MongoRepository<Showtime, String> {
    List<Showtime> findByMovieId(String movieId);
    List<Showtime> findByScreenId(String screenId);
    List<Showtime> findByMovieIdAndScreenId(String movieId, String screenId);
}
