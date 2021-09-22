package com.unipi.movierecommenderspringboot.model;

public class MovieRating {

    private Movie movie;
    private double averageRating;

    public MovieRating(Movie movie, double averageRating) {
        this.movie = movie;
        this.averageRating = averageRating;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
