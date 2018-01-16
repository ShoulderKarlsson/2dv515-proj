package main.lib;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class Filehandler {

    public static HashMap<String, User> generateUsers(HashMap<Integer, String> movies) throws IOException {
        File ratings = ResourceUtils.getFile("classpath:x_ratings.csv");
        HashMap<String, User> userMap = new HashMap<>();
        Files.lines(ratings.toPath())
                .skip(1)
                .map(line -> line.split(","))
                .forEach(info -> {
                    int userId = Integer.parseInt(info[0]);
                    int movieId = Integer.parseInt(info[1]);
                    double rating = Double.parseDouble(info[2]);

                    User u = null;
                    if (userMap.containsKey("User" + userId)) {
                        u = userMap.get("User" + userId);
                    } else {
                        u = new User("User" + userId);
                        userMap.put("User" + userId, u);
                    }

                    // Adding ratings and movies into hashmap
                    u.add_rating(movies.get(movieId), rating);
                });

        return userMap;
    }

    public static HashMap<Integer, String> generateMovies() throws IOException {
        File movies = ResourceUtils.getFile("classpath:x_movies.csv");
        HashMap<Integer, String> movieMap = new HashMap<>();
        Files.lines(movies.toPath())
                .skip(1)
                .map(line -> line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"))
                .forEach(movie -> {
                    int movieId = Integer.parseInt(movie[0]);
                    String movieName = movie[1];
                    movieMap.put(movieId, movieName);
                });


        return movieMap;
    }
}
