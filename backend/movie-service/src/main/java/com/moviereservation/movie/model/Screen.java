package com.moviereservation.movie.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "screens")
@Data
public class Screen {
    @Id
    private String id;
    private String name;
    private int rows;
    private int seatPerRow;
    private boolean active;
}
