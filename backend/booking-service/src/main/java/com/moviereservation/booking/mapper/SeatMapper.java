package com.moviereservation.booking.mapper;

import com.moviereservation.booking.model.Seat;
import com.moviereservation.booking.dto.response.SeatResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    SeatResponse toResponse(Seat seat);
}