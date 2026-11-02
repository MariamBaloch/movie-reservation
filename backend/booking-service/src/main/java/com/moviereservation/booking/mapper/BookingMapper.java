package com.moviereservation.booking.mapper;

import com.moviereservation.booking.model.Booking;
import com.moviereservation.booking.dto.response.BookingResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingResponse toResponse(Booking booking);
}