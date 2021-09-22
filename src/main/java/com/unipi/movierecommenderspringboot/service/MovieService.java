package com.unipi.movierecommenderspringboot.service;

import com.unipi.movierecommenderspringboot.model.Movie;
import com.unipi.movierecommenderspringboot.model.MovieRating;
import com.unipi.movierecommenderspringboot.model.Rater;
import com.unipi.movierecommenderspringboot.model.Rating;
import com.unipi.movierecommenderspringboot.repository.MovieDatabase;
import com.unipi.movierecommenderspringboot.repository.RaterDatabase;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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

    public MovieRating getMovieRatingById(String id) {

        Movie movie = MovieDatabase.getMovie(id);
        return new MovieRating(movie, getAverageByID(movie.getID()));

    }

    public List<MovieRating> getAllMoviesAndAverageRatings() {

        List<Movie> movies = MovieDatabase.getMovies();
        List<MovieRating> movieRatings = new ArrayList<>();

        movies.forEach(
                movie -> {
                    Rating rating = new Rating(movie.getID(),
                            getAverageByID(movie.getID()));
                    movieRatings.add(new MovieRating(movie, rating.getValue()));
                }
        );
        movieRatings.sort(Comparator.comparing(MovieRating::getAverageRating).reversed());
        return movieRatings;
    }

    // Average movie rating by ID
    // movieId: id, number of raters: minimalRaters
    private double getAverageByID(String id) {

        List<Rater> raters = RaterDatabase.getRaters();

        AtomicReference<Double> sum = new AtomicReference<>(0.0);
        AtomicInteger count = new AtomicInteger();

        raters.forEach(
                rater -> {
                    if (rater.hasRating(id)) {
                        sum.updateAndGet(v -> v + rater.getRating(id));
                        count.addAndGet(1);
                    }
                }
        );
        return (count.get() >= RecommendationService.MINIMAL_RATERS)
                ? sum.get() / count.get()
                : 0.0;
    }
}
