package com.moviereservation.booking.repository;

import com.moviereservation.booking.enums.SeatStatus;
import com.moviereservation.booking.model.Seat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;

public interface SeatRepository extends MongoRepository<Seat, String> {

    List<Seat> findAllByShowtimeIdOrderBySeatNumberAsc(String showtimeId);

    @Query("{ 'showtimeId': ?0, 'seatNumber': ?1, 'status': ?2 }")
    @Update("{ '$set': { 'status': ?3, 'bookingId': ?4 } }")
    long reserveSeat(String showtimeId, String seatNumber, SeatStatus currentStatus, SeatStatus newStatus, String bookingId);

    @Query("{ 'showtimeId': ?0, 'bookingId': ?1, 'status': 'BOOKED' }")
    @Update("{ '$set': { 'status': 'AVAILABLE' }, '$unset': { 'bookingId': '' } }")
    long releaseBookedSeats(String showtimeId, String bookingId);
}