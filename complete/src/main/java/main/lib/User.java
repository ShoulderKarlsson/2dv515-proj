package main.lib;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private final String id;
    public HashMap<String, Double> _ratings = new HashMap<>();

    public ArrayList<Rating> ratings = new ArrayList<>();


    public User(String id) {
        this.id = id;
    }


    public void add_rating(String movie, double score) {
        _ratings.put(movie, score);
    }

    public void addRating(String movie, double score) {
        this.ratings.add(new Rating(movie, score));
    }

    public String getUsername() {
        return this.id;
    }

    public HashMap<String, Double> get_ratings() {
        return _ratings;
    }

    public double getRating(String entryKey) {
        return _ratings.get(entryKey);
    }

    public Rating _getRating(String movie) {
        for (Rating r : ratings) {
            if (r.movie.equals(movie)) {
                return r;
            }
        }

        return null;
    }
}
