package com.moviereservation.movie.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection="movies")
@Data
public class Movie {
    @Id
    private String id;
    private String title;
    private String description;
    private int duration;
    private String posterUrl;
    private List<String> genres;
    private boolean active=true;
}
