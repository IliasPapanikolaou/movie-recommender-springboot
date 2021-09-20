package com.unipi.movierecommenderspringboot.repository;

import com.unipi.movierecommenderspringboot.filters.Filter;
import com.unipi.movierecommenderspringboot.model.Movie;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MovieDatabase {
    private static HashMap<String, Movie> ourMovies;

    public static void initialize(String moviefile) {
        if (ourMovies == null) {
            ourMovies = new HashMap<String,Movie>();
            loadMovies("data/" + moviefile);
        }
    }

    private static void initialize() {
        if (ourMovies == null) {
            ourMovies = new HashMap<String,Movie>();
            loadMovies("data/ratedmoviesfull.csv");
        }
    }

    private static void loadMovies(String filename) {
        List<Movie> list = loadMoviesFromCSV(filename);
        for (Movie m : list) {
            ourMovies.put(m.getID(), m);
        }
    }

    // Load movies from CSV
    private static List<Movie> loadMoviesFromCSV(String filename) {
        List<Movie> movies = new ArrayList<>();
        try {
            Reader in = new FileReader(filename);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("id", "title", "year", "country", "genre", "director",
                            "minutes", "poster")
                    .withFirstRecordAsHeader()
                    .parse(in);
            for (CSVRecord record : records) {
                Movie movie = new Movie(
                        record.get("id"),
                        record.get("title"),
                        record.get("year"),
                        record.get("country"),
                        record.get("genre"),
                        record.get("director"),
                        record.get("minutes"),
                        record.get("poster")
                );
                movies.add(movie);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public static List<Movie> getMovies() {
        initialize();
        return new ArrayList<>(ourMovies.values());
    }

    public static boolean containsID(String id) {
        initialize();
        return ourMovies.containsKey(id);
    }

    public static int getYear(String id) {
        initialize();
        return ourMovies.get(id).getYear();
    }

    public static String getGenres(String id) {
        initialize();
        return ourMovies.get(id).getGenres();
    }

    public static String getTitle(String id) {
        initialize();
        return ourMovies.get(id).getTitle();
    }

    public static Movie getMovie(String id) {
        initialize();
        return ourMovies.get(id);
    }

    public static String getPoster(String id) {
        initialize();
        return ourMovies.get(id).getPoster();
    }

    public static int getMinutes(String id) {
        initialize();
        return ourMovies.get(id).getMinutes();
    }

    public static String getCountry(String id) {
        initialize();
        return ourMovies.get(id).getCountry();
    }

    public static String getDirector(String id) {
        initialize();
        return ourMovies.get(id).getDirector();
    }

    public static int size() {
        return ourMovies.size();
    }

    public static List<String> filterBy(Filter f) {
        initialize();
        List<String> list = new ArrayList<>();
        for(String id : ourMovies.keySet()) {
            if (f.satisfies(id)) {
                list.add(id);
            }
        }
        return list;
    }

}
