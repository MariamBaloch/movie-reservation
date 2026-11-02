package com.moviereservation.movie.util;

import java.util.ArrayList;
import java.util.List;

public class SeatNumberGenerator {
    public static List<String> generate(int rows, int seatsPerRow) {

        List<String> seatNumbers = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            char rowLetter =(char) ('A' + i);

            for (int j = 1; j <= seatsPerRow; j++) {
               String seatNumber = rowLetter + String.valueOf(j);
               seatNumbers.add(seatNumber);
            }

        }

        return seatNumbers;
    }
}
