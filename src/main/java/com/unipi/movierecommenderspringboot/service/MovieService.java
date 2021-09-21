package com.unipi.movierecommenderspringboot.service;

import com.unipi.movierecommenderspringboot.model.Movie;
import com.unipi.movierecommenderspringboot.repository.MovieDatabase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MovieService {

    public List<Movie> getAllMovies() {
        return MovieDatabase.getMovies();
    }

    public List<Movie> getMovieByName(String name) {
        List<Movie> movies = MovieDatabase.getMovies();
        return movies.stream()
                .filter(movie -> movie.getTitle()
                        .toLowerCase()
                        .contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Movie> getRandomMovies(int num) {
        List<Movie> moviesToBeRated = new ArrayList<>();
        List<Movie> movies = MovieDatabase.getMovies();
        IntStream.range(0, num).forEach(
                n -> {
                    int randInt = new Random().nextInt(movies.size());
                    if (!moviesToBeRated.contains(movies.get(randInt)))
                        moviesToBeRated.add(movies.get(randInt));
                }
        );
        return moviesToBeRated;
    }
}
