package com.unipi.movierecommenderspringboot;

import com.unipi.movierecommenderspringboot.model.Rater;
import com.unipi.movierecommenderspringboot.model.Rating;
import com.unipi.movierecommenderspringboot.repository.MovieDatabase;
import com.unipi.movierecommenderspringboot.repository.RaterDatabase;
import com.unipi.movierecommenderspringboot.service.RatingService;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RatingsServiceTesting {

    private final String MOVIES_SOURCE = "ratedmoviesfull_test.csv";
//    private final String MOVIES_SOURCE = "ratedmovies_short.csv";
    private final String RATINGS_SOURCE = "ratings_test.csv";
//    private final String RATINGS_SOURCE = "ratings_short.csv";

    private final int MINIMAL_RATERS = 3;
    private final String USER_ID = "168";
    private final int NUM_OF_SIMILAR_RATERS = 10;

    @Test
    public void printAverageRatings() {

        MovieDatabase.initialize(MOVIES_SOURCE);
        RatingService fourthRatings = new RatingService(RATINGS_SOURCE);

//        System.out.println("Number of ratings in the CSV file: " + RaterDatabase.size());
//        System.out.println("Number of movies in the CSV file: " +MovieDatabase.size());

        List<Rating> ratings = fourthRatings.getAverageRatings(MINIMAL_RATERS);
        ratings.sort(Comparator.comparing(Rating::getValue));
//        System.out.println("There are " + ratings.size()
//                + " movies with at least " + MINIMAL_RATERS
//                + " raters");
//
//        ratings.forEach(r ->
//                System.out.printf("%.2f %s\n", r.getValue(), MovieDatabase.getTitle(r.getItem()))
//        );

        assertEquals(775, ratings.size());
    }

    @Test
    public void  printSimilarRatings() {
        MovieDatabase.initialize(MOVIES_SOURCE);
        RatingService fourthRatings = new RatingService(RATINGS_SOURCE);

//        System.out.println("Number of ratings in the CSV file: " +RaterDatabase.size());
//        System.out.println("Number of movies in the CSV file: " +MovieDatabase.size());

        List<Rating> similarRatings = fourthRatings.getSimilarRatings(
                USER_ID, NUM_OF_SIMILAR_RATERS, MINIMAL_RATERS);

//        System.out.println("Found " +similarRatings.size() + " movies recommended for User-ID: "
//                + USER_ID + " with at least " + MINIMAL_RATERS + " ratings.\n" + NUM_OF_SIMILAR_RATERS
//                +" closest raters are considered.") ;
//
//        similarRatings.forEach(
//                rating -> System.out.println(rating.getValue() +" " +MovieDatabase.getTitle(rating.getItem()))
//        );

        assertEquals(115, similarRatings.size());
    }

    @Test
    public void userMovieRatingAlreadyExist() {
        String raterId = "1";
        String movieId = "0068646";
        int rating = 7;
        Boolean userRatingDoesNotExist = RaterDatabase.saveRatingToCsv(RATINGS_SOURCE, raterId, movieId, rating);
        assertEquals(false, userRatingDoesNotExist);
    }
}
