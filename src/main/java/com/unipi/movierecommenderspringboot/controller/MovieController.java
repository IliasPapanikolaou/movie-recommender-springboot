package com.unipi.movierecommenderspringboot.controller;

import com.unipi.movierecommenderspringboot.model.Movie;
import com.unipi.movierecommenderspringboot.model.MovieRating;
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

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        return !movies.isEmpty() ? new ResponseEntity<>(movies, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/search")
    public ResponseEntity<List<Movie>> getMovieByName(@RequestParam String name) {
        List<Movie> movies = movieService.getMovieByName(name);
        return !movies.isEmpty() ? new ResponseEntity<>(movies, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/average")
    public ResponseEntity<List<MovieRating>> getAllMoviesAndAverageRatings() {
        List<MovieRating> movieAverageRatings = movieService.getAllMoviesAndAverageRatings();
        return !movieAverageRatings.isEmpty()
                ? new ResponseEntity<>(movieAverageRatings, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/average/{id}")
    public ResponseEntity<MovieRating> getMovieRatingById(@PathVariable String id) {
        MovieRating movieRating =  movieService.getMovieRatingById(id);
        return movieRating != null
                ? new ResponseEntity<>(movieRating, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/random/{num}")
    public ResponseEntity<List<Movie>> getRandomMovies(@PathVariable int num) {
        List<Movie> movies = movieService.getRandomMovies(num);
        return !movies.isEmpty() ? new ResponseEntity<>(movies, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
