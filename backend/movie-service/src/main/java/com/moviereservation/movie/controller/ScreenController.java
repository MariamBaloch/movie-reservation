package com.moviereservation.movie.controller;

import com.moviereservation.movie.dto.ScreenRequest;
import com.moviereservation.movie.model.Screen;
import com.moviereservation.movie.service.ScreenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screens")
public class ScreenController {
    private final ScreenService service;

    public ScreenController(ScreenService service){
        this.service = service;
    }

    @GetMapping
    public List<Screen> findAll(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Screen findById(@RequestParam String id){
        return service.findById(id);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Screen> createScreen(@RequestBody ScreenRequest request){
        Screen screen = service.createScreen(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(screen);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public Screen updateScreen(@PathVariable String id, @RequestBody ScreenRequest request){
        return service.updateScreen(id, request);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> deleteScreen(@PathVariable String id){
        service.deleteScreen(id);
        return ResponseEntity.noContent().build();
    }
}
