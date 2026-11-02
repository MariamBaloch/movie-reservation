package com.moviereservation.booking.repository;

import com.moviereservation.booking.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findAllByUserIdOrderByCreatedAtDesc(String userId);
    Optional<Booking> findByIdAndUserId(String id, String userId);
}