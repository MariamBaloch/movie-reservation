package com.moviereservation.movie.mapper;

import com.moviereservation.movie.dto.ShowtimeRequest;
import com.moviereservation.movie.dto.ShowtimeResponse;
import com.moviereservation.movie.model.Showtime;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShowtimeMapper {
    Showtime toEntity(ShowtimeRequest request);

    ShowtimeResponse toResponse(Showtime showtime);

    void updateEntity(
            ShowtimeRequest request,
            @MappingTarget Showtime showtime
    );
}
