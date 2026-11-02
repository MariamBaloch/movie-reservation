package com.moviereservation.movie.service;

import com.moviereservation.movie.dto.MovieRequest;
import com.moviereservation.movie.model.Movie;
import com.moviereservation.movie.repository.MovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MovieService {
    private final MovieRepository repository;

    public MovieService(MovieRepository repository){
        this.repository=repository;
    }

    public List<Movie> findAll(){
        return repository.findByActiveTrue();
    }

    public Movie findById(String id){
        return repository.findByIdAndActiveTrue(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
    }

    public Movie createMovie(MovieRequest request){
        Movie movie = new Movie();
        movie.setTitle(request.title());
        movie.setDescription(request.description());
        movie.setDuration(request.duration());
        movie.setPosterUrl(request.posterUrl());
        movie.setGenres(request.genres());
        return repository.save(movie);
    }

    public Movie updateMovie(String id, MovieRequest request){
        Movie movie = findById(id);
        movie.setTitle(request.title());
        movie.setDescription(request.description());
        movie.setDuration(request.duration());
        movie.setPosterUrl(request.posterUrl());
        movie.setGenres(request.genres());
        return repository.save(movie);
    }

    public void deleteMovie(String id){
        Movie movie = findById(id);
        movie.setActive(false);
        repository.save(movie);
    }

}
