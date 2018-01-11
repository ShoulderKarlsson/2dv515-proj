package main.lib;

import org.springframework.util.ResourceUtils;

import javax.swing.text.AbstractDocument;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

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

//                        u.addRating(new Rating(this.movies.get(movieId), rating));
                        u.add_rating(this.movies.get(movieId), rating);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Something> getRecomendationFor(String name) {
        User user = db.get(name);
        ArrayList<Result> res = new ArrayList<>();
        for (String k : db.keySet()) {
            // Preventing comparing user with itself.
            if (db.get(k).getUsername().equals(user.getUsername())) continue;

            User otherUser = db.get(k);
            res.add(new Result(otherUser, _calc_Euclidian(otherUser, user)));
        }

        /**
         * This contains the wheightedscore for each of the users.
         * The weighted score is calculated with the similarity * the rating a user has given for a specific movie.
         *
         * User - a user
         * HashMap<String, Double>
         *      String => Name of the movie
         *      Double => Calculated score
         */
        HashMap<User, HashMap<String, Double>> weightedScoreForUsersPerMovie = new HashMap<>();
        for (Result r : res) {
            HashMap<String, Double> userRatings = r.user.get_ratings();
            HashMap<String, Double> weightedScores = new HashMap<>();
            userRatings.keySet().stream()
                    .forEach(movieName -> {
                        double score = r.user.getRating(movieName);
                        weightedScores.put(movieName, r.simScore * score);
                    });

            weightedScoreForUsersPerMovie.put(r.user, weightedScores);
        }

        /**
         * This contains the total weightedscore for a single movie.
         * This is done by adding together they wheighted scores for all movies and all of the scores.
         * HashMap<String, Double>
         *     String => Name of the movie
         *     Double => Total weighted score for the movie
         */
        HashMap<String, Double> movieTotalWeightedScore = new HashMap<>();
        weightedScoreForUsersPerMovie.keySet().stream()
                .forEach(u -> {
                    HashMap<String, Double> userWeightedScores = weightedScoreForUsersPerMovie.get(u);
                    userWeightedScores.keySet().stream()
                            .forEach(movieName -> {

                                // If the set already contains a score for the movie
                                // update it by increasing the total score
                                if (movieTotalWeightedScore.containsKey(movieName)) {
                                    double currentMovieScore = movieTotalWeightedScore.get(movieName);
                                    currentMovieScore += userWeightedScores.get(movieName);
                                    movieTotalWeightedScore.replace(movieName, currentMovieScore);
                                } else {
                                    movieTotalWeightedScore.put(movieName, userWeightedScores.get(movieName));
                                }
                            });
                });


        for (Iterator<Map.Entry<String, Double>> it = movieTotalWeightedScore.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Double> entry = it.next();
            if (entry.getValue() == 0) {
                it.remove();
            }
        }


        /**
         * This contains the sum of all similarities for the users that has rated that movie.
         * HashMap<String, Double>
         *     String => Name of the movie
         *     Double => Sum of the similarity
         */
        HashMap<String, Double> movieSumOfSimilarities = new HashMap<>();
        movies.keySet().stream()
                .forEach(movieId -> {
                    String movieName = movies.get(movieId);
                    weightedScoreForUsersPerMovie.keySet().stream()
                            .forEach(u -> {
                                // if the user has rated the current movie
                                // add its similiarity score to the total for that movie
                                if (u.get_ratings().containsKey(movieName)) {
                                    if (movieSumOfSimilarities.containsKey(movieName)) {
                                        double movieSumSim = movieSumOfSimilarities.get(movieName);
                                        movieSumSim += u.get_ratings().get(movieName);
                                        movieSumOfSimilarities.replace(movieName, movieSumSim);
                                    } else {
                                        movieSumOfSimilarities.put(movieName, u.get_ratings().get(movieName));
                                    }
                                }
                            });
                });


        /**
         * Calculates the final result for the weighted score caluclations
         * Uses the total weightedscore for movie A and divides it with the sum of similiarites for movie A
         *
         * HashMap><String, Double>
         *     String => Name of movie
         *     Double => final weighted scores
         */
        HashMap<String, Double> totalDividedBySimSum = new HashMap<>();
        movieTotalWeightedScore
                .keySet().stream()
                .forEach(movieName -> {
                    double ws = movieTotalWeightedScore.get(movieName);
                    double movieSumSim = movieSumOfSimilarities.get(movieName);

                    totalDividedBySimSum.put(movieName, ws / movieSumSim);
                });


        ArrayList<Something> movieRecs = new ArrayList<>();
        for (Map.Entry<String, Double> entry : totalDividedBySimSum.entrySet()) {
            double d = entry.getValue();
            movieRecs.add(new Something(entry));
        }

        Collections.sort(movieRecs);
//        Collections.reverse(movieRecs);
        System.out.println(5);
        return movieRecs;
    }

    private double _calc_Euclidian(User A, User B) {
        double sim = 0.0;
        int cnt_sim = 0;

        HashMap<String, Double> BRatings = B.get_ratings();

        for (Map.Entry<String, Double> rA : A.get_ratings().entrySet()) {
            for (Map.Entry<String, Double> rB : B.get_ratings().entrySet()) {
                if (BRatings.containsKey(rA.getKey())) {
                    sim += Math.pow(
                            A.getRating(rA.getKey()) -
                            B.getRating(rB.getKey()),
                            2
                    );
                    cnt_sim++;
                }
            }
        }

        return cnt_sim == 0 ? 0 : 1.0 / (1.0 + sim);
    }
}
