package com.moviereservation.movie.service;

import com.moviereservation.movie.dto.ShowtimeRequest;
import com.moviereservation.movie.dto.ShowtimeResponse;
import com.moviereservation.movie.exception.ResourceNotFoundException;
import com.moviereservation.movie.mapper.ShowtimeMapper;
import com.moviereservation.movie.model.Showtime;
import com.moviereservation.movie.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShowtimeService {
    private final ShowtimeMapper  showtimeMapper;
    private final ShowtimeRepository showtimeRepository;

    public ShowtimeResponse createShowtime(ShowtimeRequest request){
        Showtime showtime = showtimeMapper.toEntity(request);
        Showtime saved = showtimeRepository.save(showtime);

        return showtimeMapper.toResponse(saved);
    }
    public List<ShowtimeResponse> getAll(){
        return showtimeRepository.findAll().stream().map(showtimeMapper::toResponse).toList();
    }

    public ShowtimeResponse getById(String id){
        Showtime showtime= showtimeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Showtime not found for id: "+ id));
        return showtimeMapper.toResponse(showtime);
    }

    public List<ShowtimeResponse> getByMovieId(String movieId){
        List<Showtime> showtimes = showtimeRepository.findByMovieId(movieId);
        return showtimes.stream().map(showtimeMapper::toResponse).toList();
    }

    public ShowtimeResponse updateShowtime(String id, ShowtimeRequest request){
        Showtime showtime= showtimeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Showtime not found for id: "+ id));
        showtimeMapper.updateEntity(request, showtime);
        Showtime saved= showtimeRepository.save(showtime);

        return showtimeMapper.toResponse(saved);
    }
    public void deleteShowtime(String id){
        showtimeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Showtime not found for id: "+ id));
        showtimeRepository.deleteById(id);

        log.info("Showtime with id: {} has been deleted", id);
    }

}
