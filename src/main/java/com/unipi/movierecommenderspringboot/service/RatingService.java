package com.unipi.movierecommenderspringboot.service;

import com.unipi.movierecommenderspringboot.filters.Filter;
import com.unipi.movierecommenderspringboot.filters.TrueFilter;
import com.unipi.movierecommenderspringboot.model.Rater;
import com.unipi.movierecommenderspringboot.model.Rating;
import com.unipi.movierecommenderspringboot.model.UserRating;
import com.unipi.movierecommenderspringboot.repository.MovieDatabase;
import com.unipi.movierecommenderspringboot.repository.RaterDatabase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class RatingService {

    public RatingService() {
        // default constructor
        this("ratings.csv");
    }

    public RatingService(String ratingfile) {
        RaterDatabase.initialize(ratingfile);
    }

    public int getRaterSize() {
        return RaterDatabase.size();
    }

    // Average movie rating by ID
    // movieId: id, number of raters: minimalRaters
    private double getAverageByID(String id, int minimalRaters) {

        AtomicReference<Double> sum = new AtomicReference<>(0.0);
        AtomicInteger count = new AtomicInteger();

        RaterDatabase.getRaters().forEach(
                rater -> {
                    if (rater.hasRating(id)) {
                        sum.updateAndGet(v -> v + rater.getRating(id));
                        count.addAndGet(1);
                    }
                }
        );
        return (count.get() >= minimalRaters) ? sum.get() / count.get() : 0.0;
    }

    // Average rating for every movie that has been rated by at least minimalRaters rater
    public List<Rating> getAverageRatings(int minimalRaters) {

        List<String> movies = MovieDatabase.filterBy(new TrueFilter());
        List<Rating> avgRatings = new ArrayList<>();

        movies.forEach(
                id -> {
                    double avg = Math.round(getAverageByID(id, minimalRaters) * 100.0) / 100.0;
                    if (avg != 0.0) {
                        Rating rating = new Rating(id, avg);
                        avgRatings.add(rating);
                    }
                }
        );
        return avgRatings;
    }

    public List<Rating> getAverageRatingsByFilter(int minimalRaters, Filter filterCriteria) {
        List<String> filteredMovies = MovieDatabase.filterBy(filterCriteria);
        List<Rating> averageRatings = new ArrayList<>();

        filteredMovies.forEach(
                id -> {
                    double avg = Math.round(getAverageByID(id, minimalRaters) * 100.0) / 100.0;
                    if (avg != 0.0) {
                        Rating rating = new Rating(id, avg);
                        averageRatings.add(rating);
                    }
                }
        );
        return averageRatings;
    }

    // Translate a rating from the scale 0 to 10 to the scale -5 to 5
    // and return the dot product of the ratings of movies that they are both rated
    private double dotProduct(Rater me, Rater r) {
        AtomicReference<Double> dotProduct = new AtomicReference<>(0.0);
        List<String> itemsRatedByMe = me.getItemsRated();

        itemsRatedByMe.forEach(
                item -> {
                    if (r.getItemsRated().contains(item)) {
                        double myRating = r.getRating(item);
                        double rRating = me.getRating(item);
                        // dotProduct += (rRating - 5.0) * (myRating - 5.0);
                        dotProduct.updateAndGet(v -> (v + (rRating - 5.0) * (myRating - 5.0)));
                    }
                }
        );
        return dotProduct.get();
    }

    public List<Rating> getSimilarities(String id) {
        List<Rating> similarities = new ArrayList<>();
        Rater me = RaterDatabase.getRater(id);

        RaterDatabase.getRaters().forEach(
                rater -> {
                    // if it is not me
                    if (!rater.getID().equals(id)) {
                        double dotProduct = dotProduct(me, rater);
                        if (dotProduct >= 0) {
                            similarities.add(new Rating(rater.getID(), dotProduct));
                        }
                    }
                }
        );
        similarities.sort(Collections.reverseOrder());
        return similarities;
    }

    public List<Rating> getSimilarRatings(String id, int numSimilarRaters, int minimalRaters) {
        List<Rating> weightedRatings = new ArrayList<>();
        List<Rating> similarRaters = getSimilarities(id);
        List<String> movies = MovieDatabase.filterBy(new TrueFilter());

        HashMap<String, Double> accumulatedRating = new HashMap<>();
        HashMap<String, Integer> accumulatedCount = new HashMap<>();

        AtomicReference<Double> currentRating = new AtomicReference<>(0.0);
        AtomicInteger currentCount = new AtomicInteger();

        movies.forEach(
                movie -> {
                    currentRating.set(0.0);
                    currentCount.set(0);
                    IntStream.range(0, numSimilarRaters).forEach(
                            num -> {
                                Rating rating = similarRaters.get(num);
                                String raterId = rating.getItem();
                                double weight = rating.getValue();

                                Rater rater = RaterDatabase.getRater(raterId);

                                if (rater.hasRating(movie)) {
                                    double ratingValue = rater.getRating(movie) * weight;
                                    currentRating.updateAndGet(v -> (v + ratingValue));
                                    currentCount.addAndGet(1);
                                }
                            }
                    );

                    if (currentCount.get() >= minimalRaters) {
                        accumulatedRating.put(movie, currentRating.get());
                        accumulatedCount.put(movie, currentCount.get());
                    }
                }
        );

        accumulatedRating.keySet().forEach(
                movie -> {
                    double weightedRating = Math.round(
                            (accumulatedRating.get(movie) / accumulatedCount.get(movie)) * 100.0) / 100.0;
                    Rating rating = new Rating(movie, weightedRating);
                    weightedRatings.add(rating);
                }
        );
        weightedRatings.sort(Collections.reverseOrder());
        return weightedRatings;
    }

    // Get Similarities from DataBase by providing new user ratings without being stored in DataBase
    private List<Rating> getSimilaritiesByUserRating(UserRating userRating) {

        List<Rating> similarities = new ArrayList<>();
        // We consider the user who is not stored in DataBase as user with id = ""
        Rater me = new Rater("");

        userRating.getRatings().forEach(
                rating -> {
                    me.addRating(rating.getItem(), rating.getValue());
                }
        );

        RaterDatabase.getRaters().forEach(
                rater -> {
                    double dotProduct = dotProduct(me, rater);
                    if (dotProduct >= 0) {
                        similarities.add(new Rating(rater.getID(), dotProduct));
                    }
                }
        );
        similarities.sort(Collections.reverseOrder());
        return similarities;
    }

    // Get Similar Ratings from DataBase by providing new user ratings without being stored in DataBase
    public List<Rating> getSimilarRatingsByUserRating(UserRating userRating, int numSimilarRaters, int minimalRaters) {
        List<Rating> weightedRatings = new ArrayList<>();
        List<Rating> similarRaters = getSimilaritiesByUserRating(userRating);
        List<String> movies = MovieDatabase.filterBy(new TrueFilter());

        HashMap<String, Double> accumulatedRating = new HashMap<>();
        HashMap<String, Integer> accumulatedCount = new HashMap<>();

        AtomicReference<Double> currentRating = new AtomicReference<>(0.0);
        AtomicInteger currentCount = new AtomicInteger();

        movies.forEach(
                movie -> {
                    currentRating.set(0.0);
                    currentCount.set(0);
                    IntStream.range(0, numSimilarRaters).forEach(
                            num -> {
                                Rating rating = similarRaters.get(num);
                                String raterId = rating.getItem();
                                double weight = rating.getValue();

                                Rater rater = RaterDatabase.getRater(raterId);

                                if (rater.hasRating(movie)) {
                                    double ratingValue = rater.getRating(movie) * weight;
                                    currentRating.updateAndGet(v -> (v + ratingValue));
                                    currentCount.addAndGet(1);
                                }
                            }
                    );

                    if (currentCount.get() >= minimalRaters) {
                        accumulatedRating.put(movie, currentRating.get());
                        accumulatedCount.put(movie, currentCount.get());
                    }
                }
        );

        accumulatedRating.keySet().forEach(
                movie -> {
                    double weightedRating = Math.round(
                            (accumulatedRating.get(movie) / accumulatedCount.get(movie)) * 100.0) / 100.0;
                    Rating rating = new Rating(movie, weightedRating);

                    weightedRatings.add(rating);
                }
        );

        weightedRatings.sort(Collections.reverseOrder());
        return weightedRatings;
    }

    public List<Rating> getSimilarRatingsByFilter(
            String id, int numSimilarRaters, int minimalRaters, Filter filterCriteria) {
        List<Rating> weightedRatings = new ArrayList<>();
        List<Rating> similarRaters = getSimilarities(id);
        List<String> filteredMovies = MovieDatabase.filterBy(filterCriteria);

        HashMap<String, Double> accumulatedRating = new HashMap<>();
        HashMap<String, Integer> accumulatedCount = new HashMap<>();

        AtomicReference<Double> currentRating = new AtomicReference<>(0.0);
        AtomicInteger currentCount = new AtomicInteger();

        filteredMovies.forEach(
                movie -> {
                    currentRating.set(0.0);
                    currentCount.set(0);
                    IntStream.range(0, numSimilarRaters).forEach(
                            num -> {
                                Rating rating = similarRaters.get(num);
                                String raterId = rating.getItem();
                                double weight = rating.getValue();

                                Rater rater = RaterDatabase.getRater(raterId);

                                if (rater.hasRating(movie)) {
                                    double ratingValue = rater.getRating(movie) * weight;
                                    currentRating.updateAndGet(v -> (v + ratingValue));
                                    currentCount.addAndGet(1);
                                }
                            }
                    );

                    if (currentCount.get() >= minimalRaters) {
                        accumulatedRating.put(movie, currentRating.get());
                        accumulatedCount.put(movie, currentCount.get());
                    }
                }
        );

        accumulatedRating.keySet().forEach(
                movie -> {
                    double weightedRating = Math.round(
                            (accumulatedRating.get(movie) / accumulatedCount.get(movie)) * 100.0) / 100.0;
                    Rating rating = new Rating(movie, weightedRating);
                    weightedRatings.add(rating);
                }
        );
        weightedRatings.sort(Collections.reverseOrder());
        return weightedRatings;
    }
}
