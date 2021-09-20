package com.unipi.movierecommenderspringboot.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Rater {

    private String myID;
    private HashMap<String, Rating> myRatings;

    public Rater(String id) {
        myID = id;
        myRatings = new HashMap<>();
    }

    public void addRating(String item, double rating) {
        myRatings.put(item, new Rating(item,rating));
    }

    public boolean hasRating(String item) {

        return myRatings.containsKey(item);
    }

    public String getID() {
        return myID;
    }

    public double getRating(String item) {

        return (hasRating(item)) ? myRatings.get(item).getValue() : -1;
    }

    public int numRatings() {
        return myRatings.size();
    }

    public ArrayList<String> getItemsRated() {
        ArrayList<String> list = new ArrayList<>();

        myRatings.keySet().forEach((k) ->
                list.add(myRatings.get(k).getItem())
        );
        return list;
    }
}
