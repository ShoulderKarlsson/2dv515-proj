package main.lib;

import java.io.IOException;
import java.util.*;

public class Recommender {
    HashMap<String, User> db = new HashMap<>();
    HashMap<Integer, String> movies = new HashMap<>();


    public Recommender() {
        try {
            this.movies = Filehandler.generateMovies();
            this.db = Filehandler.generateUsers(movies);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, HashMap<String, Double>> getRecomendationFor(String name) {
        User user = db.get(name);
        ArrayList<Result> similarityScores = new ArrayList<>();
        for (String k : db.keySet()) {
            // Preventing comparing user with itself.
            if (db.get(k).getUsername().equals(user.getUsername())) continue;

            User otherUser = db.get(k);
            similarityScores.add(new Result(otherUser, _calc_Euclidian(otherUser, user)));
        }

        /**
         * This contains the weightedscores for a user per movie
         * this is calculated with the help of similarity.
         * movierating * similarity score = weighted score
         *
         * HashMap
         *  String => Name of a user
         *  HashMap<String, Double>
         *      String => Name of a movie
         *      Double => weightedscore for that movie for the specific user
         */
        HashMap<String, HashMap<String, Double>> userWightedScorePerMovie = new HashMap<>();
        for (Result r : similarityScores) {
            HashMap<String, Double> movieScore = new HashMap<>();
            HashMap<String, Double> userRatings = r.user.get_ratings();
            userRatings.keySet().stream().forEach(movieName -> {
                double movieRating = userRatings.get(movieName);
                movieScore.put(movieName, r.simScore * movieRating);
            });
            userWightedScorePerMovie.put(r.user.getUsername(), movieScore);
        }

        /**
         * Contains the the total weightedScores from all users
         * That has rated a specific movie
         */
        HashMap<String, Double> totalWeightedScoreForEachMovie = new HashMap<>();
        for (int movieId : movies.keySet()) {
            String movieName = movies.get(movieId);
            double movieScore = 0.0;
            for (String username : userWightedScorePerMovie.keySet()) {
                HashMap<String, Double> userWeightedScores = userWightedScorePerMovie.get(username);

                if (userWeightedScores.containsKey(movieName)) {
                    movieScore += userWeightedScores.get(movieName);
                }
            }

            totalWeightedScoreForEachMovie.put(movieName, movieScore);
        }


        /**
         * Calculates the total similarity score for each user
         * HashMap:
         *  String => Name of a movie
         *  Double => The sum of all similarities
         */
        HashMap<String, Double> sumSims = new HashMap<>();
        for (int movieId : movies.keySet()) {
            double totalSim = 0.0;
            for (String username : userWightedScorePerMovie.keySet()) {
                HashMap<String, Double> userWeightedScores = userWightedScorePerMovie.get(username);
                // If the weighted score is not 0 on the movie, add it to total
                if (userWeightedScores.containsKey(movies.get(movieId))) {
                    if (userWeightedScores.get(movies.get(movieId)) != 0) {
                        for (Result r : similarityScores) {
                            if (r.user.getUsername().equals(username)) {
                                totalSim += r.simScore;
                            }
                        }
                    }
                }

            }

            sumSims.put(movies.get(movieId), totalSim);
        }

        HashMap<String, Double> totalSimSum = new HashMap<>();
        sumSims.forEach((key, value) -> {
//            if (totalSimSum.size() < 100) {
                double total = totalWeightedScoreForEachMovie.get(key);
                totalSimSum.put(key, total / value);
//            }
        });


        HashMap<String, HashMap<String, Double>> result = new HashMap<>();
        HashMap<String, Double> userSims = new HashMap<>();
        for (Result r : similarityScores) {
//            if (userSims.size() < 100) {
                userSims.put(r.user.getUsername(), r.simScore);
//            } else {
//                break;
//            }
        }
        result.put("userRecs", userSims);
        result.put("movieRecs", totalSimSum);

        return result;
    }

    private double _calc_Euclidian(User A, User B) {
        double sim = 0.0;
        int cntSim = 0;

        HashMap<String, Double> a_ratings = A.get_ratings();
        HashMap<String, Double> b_ratings = B.get_ratings();

        for (String a_key : a_ratings.keySet()) {
            for (String b_key : b_ratings.keySet()) {
                if (a_key.equals(b_key) && b_ratings.get(b_key) != 0 && a_ratings.get(a_key) != 0) {
                    double a_currentRatingScore = a_ratings.get(a_key);
                    double b_currentRatingScore = b_ratings.get(b_key);
                    sim += Math.pow(a_currentRatingScore - b_currentRatingScore, 2);
                    cntSim++;
                }
            }
        }

        return cntSim == 0 ? 0 : 1.0 / (1.0 + sim);
    }
}
