package main.lib;

import java.io.Serializable;
import java.util.ArrayList;

public class User {
    private final String user;
    public ArrayList<Rating> ratings = new ArrayList<>();

    public User(String user) {
        this.user = user;
    }

    public void addRating(Rating r) {
        ratings.add(r);
    }

    /**
     * Change this to different datastructure in order to enhance performance?
     */
    public Rating getRatingFor(String movie) {
        Rating rating = null;

        for (Rating r : ratings) {
            if (r.movie.equals(movie)) {
                rating = r;
                break;
            }
        }

        return rating;
    }

    public String getUsername() {
        return this.user;
    }
}
