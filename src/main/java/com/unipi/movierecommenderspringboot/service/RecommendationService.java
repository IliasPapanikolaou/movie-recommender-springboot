package com.unipi.movierecommenderspringboot.service;

import com.unipi.movierecommenderspringboot.model.*;
import com.unipi.movierecommenderspringboot.repository.MovieDatabase;
import com.unipi.movierecommenderspringboot.repository.RaterDatabase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {

    private final int MINIMAL_RATERS = 3;
    private final int NUM_OF_SIMILAR_RATERS = 10;

    RatingService ratingService;
    List<Movie> movies;
    List<Rater> raters;

    public RecommendationService(RatingService ratingService) {
        this.ratingService = ratingService;
        this.movies = new ArrayList<>(MovieDatabase.getMovies());
        this.raters = new ArrayList<>(RaterDatabase.getRaters());
    }

    public List<Recommendation> getRecommendationsById(String webRaterId) {
        List<Rating> similarRatings = ratingService.getSimilarRatings(
                webRaterId, NUM_OF_SIMILAR_RATERS, MINIMAL_RATERS);

        List<Recommendation> recommendations = new ArrayList<>();

        if (similarRatings.size() != 0) {
            similarRatings.forEach( rating -> {
                Movie movie = MovieDatabase.getMovie(rating.getItem());
                recommendations.add(
                        new Recommendation(movie, rating.getValue()));
            });
        }
        return recommendations;
    }

    public List<Recommendation> getRecommendationsByUserRating(UserRating userRating) {
        List<Rating> similarRatings = ratingService.getSimilarRatingsByUserRating(
                userRating, NUM_OF_SIMILAR_RATERS, MINIMAL_RATERS);

        List<Recommendation> recommendations = new ArrayList<>();

        if (similarRatings.size() != 0) {
            similarRatings.forEach( rating -> {
                // Exclude movies already rated for showing up
                if(!userRating.compare(rating)) {
                    Movie movie = MovieDatabase.getMovie(rating.getItem());
                    recommendations.add(new Recommendation(movie, rating.getValue()));
                }
            });
        }
        return recommendations;
    }
}
