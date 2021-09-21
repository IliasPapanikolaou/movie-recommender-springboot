package com.unipi.movierecommenderspringboot.controller;

import com.unipi.movierecommenderspringboot.model.Movie;
import com.unipi.movierecommenderspringboot.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        return !movies.isEmpty() ? new ResponseEntity<>(movies, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Movie>> getMovieByName(@RequestParam String name) {
        List<Movie> movies = movieService.getMovieByName(name);
        return !movies.isEmpty() ? new ResponseEntity<>(movies, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    @GetMapping("/random/{num}")
    public ResponseEntity<List<Movie>> getRandomMovies(@PathVariable int num) {
        List<Movie> movies = movieService.getRandomMovies(num);
        return !movies.isEmpty() ? new ResponseEntity<>(movies, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
