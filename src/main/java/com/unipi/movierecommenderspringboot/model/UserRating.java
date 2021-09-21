package com.unipi.movierecommenderspringboot.model;

import java.util.List;
import java.util.stream.Collectors;

public class UserRating {

    private long id;
    private List<Rating> ratings;

    public UserRating(long id, List<Rating> ratings) {
        this.id = id;
        this.ratings = ratings;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public boolean compare(Rating otherRating) {
        boolean equals = false;
        for (Rating rating : ratings) {
            if (rating.compareToOtherRating(otherRating))
                equals = true;
        }
        return equals;
    }
}
