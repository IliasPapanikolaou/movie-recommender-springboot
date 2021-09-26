package com.unipi.movierecommenderspringboot.repository;

import com.unipi.movierecommenderspringboot.commons.FileResource;
import com.unipi.movierecommenderspringboot.commons.StorageResource;
import com.unipi.movierecommenderspringboot.model.Rater;
import com.unipi.movierecommenderspringboot.model.Rating;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RaterDatabase {
    private static HashMap<String, Rater> ourRaters;

    private static void initialize() {
        // this method is only called from addRatings
        if (ourRaters == null) {
            ourRaters = new HashMap<String, Rater>();
        }
    }

    public static void initialize(String filename) {
        if (ourRaters == null) {
            ourRaters = new HashMap<String, Rater>();
            addRatings("data/" + filename);
        }
    }

    public static void addRatings(String filename) {
        initialize();
        FileResource fr = new FileResource(filename);
        CSVParser csvp = fr.getCSVParser();
        for (CSVRecord rec : csvp) {
            String id = rec.get("rater_id");
            String item = rec.get("movie_id");
            String rating = rec.get("rating");
            addRaterRating(id, item, Double.parseDouble(rating));
        }
    }

    public static boolean saveRatingToCsv(String filename, String raterId, String movieId, int rating) {
        // Check if rating for the same user and movie exists
        initialize(filename);
        if (ourRaters.containsKey(raterId)) {
            Rater rater = ourRaters.get(raterId);
            if (rater.getItemsRated().contains(movieId)) {
                System.out.println("Movie rating already exists from this rater.");
                return false;
            }
        }
        // Store Rating to CSV
        long time = Instant.now().getEpochSecond();
        try {
            BufferedWriter writer = Files.newBufferedWriter(
                    Paths.get("data/" +filename),
                    StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE
            );
//            CSVPrinter csvPrinter = new CSVPrinter(writer,
//                    CSVFormat.DEFAULT.withHeader("rater_id", "movie_id", "rating", "time"));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            csvPrinter.printRecord(raterId, movieId, rating, time);
            csvPrinter.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void addRaterRating(String raterID, String movieID, double rating) {
        initialize();
        Rater rater = null;
        if (ourRaters.containsKey(raterID)) {
            rater = ourRaters.get(raterID);
        } else {
            rater = new Rater(raterID);
            ourRaters.put(raterID, rater);
        }
        rater.addRating(movieID, rating);
    }

    public static Rater getRater(String id) {
        initialize();

        return ourRaters.get(id);
    }

    public static List<Rater> getRaters() {
        initialize();
        List<Rater> list = new ArrayList<Rater>(ourRaters.values());

        return list;
    }

    public static int size() {
        return ourRaters.size();
    }

}
