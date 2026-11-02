package com.moviereservation.movie.service;

import com.moviereservation.movie.dto.ScreenRequest;
import com.moviereservation.movie.model.Screen;
import com.moviereservation.movie.repository.ScreenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ScreenService {
    private final ScreenRepository repository;

    public ScreenService(ScreenRepository repository) {
        this.repository = repository;
    }

    public List<Screen> findAll(){
        return repository.findByActiveTrue();
    }

    public Screen findById(String id){
        return repository.findByIdAndActiveTrue(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Screen not found"));
    }

    public Screen createScreen(ScreenRequest request){
        Screen screen = new Screen();
        screen.setName(request.name());
        screen.setRows(request.rows());
        screen.setSeatPerRow(request.seatsPerRow());
        return repository.save(screen);
    }

    public Screen updateScreen(String id, ScreenRequest request){
        Screen screen = findById(id);
        if(screen.getRows()!=request.rows() || screen.getSeatPerRow()!=request.seatsPerRow()){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Screen already exists");
        }
        screen.setName(request.name());
        screen.setRows(request.rows());
        screen.setSeatPerRow(request.seatsPerRow());
        return repository.save(screen);
    }

    public void deleteScreen(String id){
        Screen screen = findById(id);
        screen.setActive(false);
        repository.save(screen);
    }
}
