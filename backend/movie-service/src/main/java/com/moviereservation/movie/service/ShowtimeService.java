package com.moviereservation.movie.service;

import com.moviereservation.movie.client.BookingServiceClient;
import com.moviereservation.movie.dto.ShowtimeRequest;
import com.moviereservation.movie.dto.ShowtimeResponse;
import com.moviereservation.movie.exception.ResourceNotFoundException;
import com.moviereservation.movie.mapper.ShowtimeMapper;
import com.moviereservation.movie.model.Screen;
import com.moviereservation.movie.model.Showtime;
import com.moviereservation.movie.model.ShowtimeStatus;
import com.moviereservation.movie.repository.MovieRepository;
import com.moviereservation.movie.repository.ScreenRepository;
import com.moviereservation.movie.repository.ShowtimeRepository;
import com.moviereservation.movie.util.SeatNumberGenerator;
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
    private final BookingServiceClient bookingServiceClient;
    private final ScreenRepository screenRepository;
    private final MovieRepository movieRepository;

    public ShowtimeResponse createShowtime(ShowtimeRequest request){
        movieRepository.findByIdAndActiveTrue(request.getMovieId()).orElseThrow(() -> new ResourceNotFoundException("Movie not found for id: " + request.getMovieId()));
        Screen screen = screenRepository.findByIdAndActiveTrue(request.getScreenId()).orElseThrow(() -> new ResourceNotFoundException("Screen not found for id: " + request.getScreenId()));

        List<String> seatNumbers = SeatNumberGenerator.generate(screen.getRows(), screen.getSeatPerRow());

        Showtime showtime = showtimeMapper.toEntity(request);
        showtime.setStatus(ShowtimeStatus.PENDING);
        Showtime saved = showtimeRepository.save(showtime);

        bookingServiceClient.initializeSeats(saved.getId(), seatNumbers);
        saved.setStatus(ShowtimeStatus.SCHEDULED);
        saved = showtimeRepository.save(saved);

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