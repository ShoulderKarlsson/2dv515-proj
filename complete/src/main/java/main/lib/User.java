package main.lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private final String user;
//    public ArrayList<Rating> ratings = new ArrayList<>();
    public HashMap<String, Double> _ratings = new HashMap<>();

    public User(String user) {
        this.user = user;
    }

//    public void addRating(Rating r) {
//        ratings.add(r);
//    }

    public void add_rating(String movie, double score) {
        _ratings.put(movie, score);
    }

    /**
     * Change this to different datastructure in order to enhance performance?
     */
//    public Rating getRatingFor(String movie) {
//        Rating rating = null;
//
//        for (Rating r : ratings) {
//            if (r.movie.equals(movie)) {
//                rating = r;
//                break;
//            }
//        }
//
//        return rating;
//    }

    public String getUsername() {
        return this.user;
    }

    public HashMap<String, Double> get_ratings() {
        return _ratings;
    }

    public double getRating(String entryKey) {
        return _ratings.get(entryKey);
    }
}
