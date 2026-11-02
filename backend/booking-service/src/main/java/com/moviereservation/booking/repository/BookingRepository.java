package com.moviereservation.booking.repository;

import com.moviereservation.booking.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findAllByUserIdOrderByCreatedAtDesc(String userId);

    Optional<Booking> findByIdAndUserId(String id, String userId);

    @Query("{ '_id': ?0, 'userId': ?1, 'status': 'CONFIRMED' }")
    @Update("{ '$set': { 'status': 'CANCELLED' } }")
    long cancelConfirmedBooking(String bookingId, String userId);
}