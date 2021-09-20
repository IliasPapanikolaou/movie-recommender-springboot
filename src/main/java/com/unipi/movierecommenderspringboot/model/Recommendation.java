package com.unipi.movierecommenderspringboot.model;

public class Recommendation {

    private Movie movie;
    private double ratingValue;

    public Recommendation(Movie movie, double ratingValue) {
        this.movie = movie;
        this.ratingValue = ratingValue;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(double ratingValue) {
        this.ratingValue = ratingValue;
    }
}
