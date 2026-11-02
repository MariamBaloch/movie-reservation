package com.moviereservation.booking.repository;

import com.moviereservation.booking.model.Seat;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface SeatRepository extends MongoRepository<Seat, String> {

    List<Seat> findAllByShowtimeIdOrderBySeatNumberAsc(String showtimeId);
}