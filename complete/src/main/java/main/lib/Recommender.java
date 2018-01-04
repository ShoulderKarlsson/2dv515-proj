package main.lib;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class Recommender {
    HashMap<String, User> db = new HashMap<>();
    HashMap<Integer, String> movies = new HashMap<>();


    public Recommender() {
        generateDb();
    }

    private void generateDb() {
        try {
            File ratings = ResourceUtils.getFile("classpath:ratings.csv");
            File movies = ResourceUtils.getFile("classpath:movies.csv");

            System.out.println("Adding all movies...");
            Files.lines(movies.toPath())
                    .skip(1)
                    .map(line -> line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"))
                    .forEach(movie -> {
                        int movieId = Integer.parseInt(movie[0]);
                        String movieName = movie[1];
                        this.movies.put(movieId, movieName);
                    });
            Files.lines(ratings.toPath())
                    .skip(1)
                    .map(line -> line.split(","))
                    .forEach(info -> {
                        int userId = Integer.parseInt(info[0]);
                        int movieId = Integer.parseInt(info[1]);
                        double rating = Double.parseDouble(info[2]);

                        User u = null;
                        if (db.containsKey("User" + userId)) {
                            u = db.get("User" + userId);
                        } else {
                            u = new User("User" + userId);
                            db.put("User" + userId, u);
                        }

                        u.addRating(new Rating(this.movies.get(movieId), rating));
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object getRecomendationFor(String name) {
        User user = db.get(name);
        ArrayList<Result> res = new ArrayList<>();
        for (String k : db.keySet()) {
            // Preventing comparing user with itself.
            if (db.get(k).getUsername().equals(user.getUsername())) continue;

            User otherUser = db.get(k);
            res.add(new Result(otherUser.getUsername(), calc_Euclidian(otherUser, user)));
        }

        System.out.println(res);

        return null;
    }

    private double calc_Euclidian(User A, User B) {
        double sim = 0.0;
        int cnt_sim = 0;

        for (Rating aRating : A.ratings) {
            for (Rating bRating : B.ratings) {
                // TODO: Redo matches? Not really sure the comparison is correct
//                if (aRating.matches(bRating)) {
                    sim += Math.pow(aRating.score - bRating.score, 2);
                    cnt_sim++;
//                }
            }
        }

        return cnt_sim == 0 ? 0 : 1.0 / (1.0 + sim);
    }
}
