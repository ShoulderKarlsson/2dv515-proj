package main.lib;

import org.springframework.util.ResourceUtils;

import javax.swing.text.AbstractDocument;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
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
            File ratings = ResourceUtils.getFile("classpath:x_ratings.csv");
            File movies = ResourceUtils.getFile("classpath:x_movies.csv");

            System.out.println("Adding all movies...");
            Files.lines(movies.toPath())
                    .skip(1)
                    .map(line -> line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"))
                    .forEach(movie -> {
                        int movieId = Integer.parseInt(movie[0]);
                        String movieName = movie[1];
                        this.movies.put(movieId, movieName);
                    });

            System.out.println("Adding all users...");
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

                        // Adding into arraylist of Ratings
//                        u.addRating(this.movies.get(movieId), rating);


                        // Adding ratings and movies into hashmap
                        u.add_rating(this.movies.get(movieId), rating);
                    });
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


        // Contains the total amount of weigthedscores from all users
        // that has rated the current movie.
        // (Total in the table)
        HashMap<String, Double> totalWeightedScoreForEachMovie = new HashMap<>();
        for (int movieId : movies.keySet()) {
            String movieName = movies.get(movieId);
            double movieScore = 0.0;
            for (String username : userWightedScorePerMovie.keySet()) {
                HashMap<String, Double> userWeightedScores = userWightedScorePerMovie.get(username);
                movieScore += userWeightedScores.get(movieName);
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
                if (userWeightedScores.get(movies.get(movieId)) != 0) {
                    for (Result r : similarityScores) {
                        if (r.user.getUsername().equals(username)) {
                            totalSim += r.simScore;
                        }
                    }
                }
            }

            sumSims.put(movies.get(movieId), totalSim);
        }


        /**
         * Total / sim_sum
         *
         * HashMap<String, Double>
         *     String => Moviename
         *     Double => Total divided by sum_sim
         */

        HashMap<String, Double> totalSimSum = new HashMap<>();
        sumSims.forEach((key, value) -> {
            double total = totalWeightedScoreForEachMovie.get(key);
            totalSimSum.put(key, total / value);
        });

        HashMap<String, HashMap<String, Double>> result = new HashMap<>();
        HashMap<String, Double> userSims = new HashMap<>();
        for (Result r : similarityScores) {
            userSims.put(r.user.getUsername(), r.simScore);
        }
        result.put("userRecs", userSims);
        result.put("movieRecs", totalSimSum);

        return result;

//        /**
//         * This contains the wheightedscore for each of the users.
//         * The weighted score is calculated with the similarity * the rating a user has given for a specific movie.
//         *
//         * User - a user
//         * HashMap<String, Double>
//         *      String => Name of the movie
//         *      Double => Calculated score
//         */
//        HashMap<User, HashMap<String, Double>> weightedScoreForUsersPerMovie = new HashMap<>();
//        for (Result r : similarityScores) {
//            HashMap<String, Double> userRatings = r.user.get_ratings();
//            HashMap<String, Double> weightedScores = new HashMap<>();
//            userRatings.keySet().stream()
//                    .forEach(movieName -> {
//                        double score = r.user._ratings.get(movieName);
//                        weightedScores.put(movieName, r.simScore * score);
//                    });
//
//            weightedScoreForUsersPerMovie.put(r.user, weightedScores);
//        }
//
//        /**
//         * This contains the total weightedscore for a single movie.
//         * This is done by adding together they wheighted scores for all movies and all of the scores.
//         * HashMap<String, Double>
//         *     String => Name of the movie
//         *     Double => Total weighted score for the movie
//         */
//        HashMap<String, Double> movieTotalWeightedScore = new HashMap<>();
//        weightedScoreForUsersPerMovie.keySet().stream()
//                .forEach(u -> {
//                    HashMap<String, Double> userWeightedScores = weightedScoreForUsersPerMovie.get(u);
//                    userWeightedScores.keySet().stream()
//                            .forEach(movieName -> {
//
//                                // If the set already contains a score for the movie
//                                // update it by increasing the total score
//                                if (movieTotalWeightedScore.containsKey(movieName)) {
//                                    double currentMovieScore = movieTotalWeightedScore.get(movieName);
//                                    currentMovieScore += userWeightedScores.get(movieName);
//                                    movieTotalWeightedScore.replace(movieName, currentMovieScore);
//                                } else {
//                                    movieTotalWeightedScore.put(movieName, userWeightedScores.get(movieName));
//                                }
//                            });
//                });
//
//
//        movieTotalWeightedScore.entrySet().removeIf(entry -> entry.getValue() == 0);
//
//
//        /**
//         * This contains the sum of all similarities for the users that has rated that movie.
//         * HashMap<String, Double>
//         *     String => Name of the movie
//         *     Double => Sum of the similarity
//         */
//        HashMap<String, Double> movieSumOfSimilarities = new HashMap<>();
//        movies.keySet().stream()
//                .forEach(movieId -> {
//                    String movieName = movies.get(movieId);
//                    weightedScoreForUsersPerMovie.keySet().stream()
//                            .forEach(u -> {
//                                // if the user has rated the current movie
//                                // add its similiarity score to the total for that movie
//                                if (u.get_ratings().containsKey(movieName)) {
//                                    if (movieSumOfSimilarities.containsKey(movieName)) {
//                                        double movieSumSim = movieSumOfSimilarities.get(movieName);
////                                        movieSumSim += u.get_ratings().get(movieName);
//                                        movieSumSim += u._ratings.get(movieName);
//                                        movieSumOfSimilarities.replace(movieName, movieSumSim);
//                                    } else {
//                                        movieSumOfSimilarities.put(movieName, u.get_ratings().get(movieName));
//                                    }
//                                }
//                            });
//                });
//
//
//        /**
//         * Calculates the final result for the weighted score caluclations
//         * Uses the total weightedscore for movie A and divides it with the sum of similiarites for movie A
//         *
//         * HashMap><String, Double>
//         *     String => Name of movie
//         *     Double => final weighted scores
//         */
//        HashMap<String, Double> totalDividedBySimSum = new HashMap<>();
//        movieTotalWeightedScore
//                .keySet().stream()
//                .forEach(movieName -> {
//                    double ws = movieTotalWeightedScore.get(movieName);
//                    double movieSumSim = movieSumOfSimilarities.get(movieName);
//
//                    totalDividedBySimSum.put(movieName, ws / movieSumSim);
//                });
//
//
//        ArrayList<Something> movieRecs = new ArrayList<>();
//        for (Map.Entry<String, Double> entry : totalDividedBySimSum.entrySet()) {
//            double d = entry.getValue();
//            movieRecs.add(new Something(entry));
//        }

//        Collections.sort(movieRecs);
//        return movieRecs;
    }

    private double _calc_Euclidian(User A, User B) {
        double sim = 0.0;
        int cntSim = 0;

        HashMap<String, Double> a_ratings = A.get_ratings();
        HashMap<String, Double> b_ratings = B.get_ratings();

        for (String a_key : a_ratings.keySet()) {
            for (String b_key : b_ratings.keySet()) {
                if (a_key.equals(b_key) && b_ratings.get(b_key) != 0 && a_ratings.get(a_key) != 0) {
//                if (a_ratings.containsKey(b_key) && b_ratings.get(b_key) != 0 && a_ratings.get(a_key) != 0) {
                    double a_currentRatingScore = a_ratings.get(a_key);
                    double b_currentRatingScore = b_ratings.get(b_key);
                    sim += Math.pow(a_currentRatingScore - b_currentRatingScore, 2);
                    cntSim++;
                }
            }
        }


        if (cntSim == 0) {
            return 0;
        }

        return 1.0 / (1.0 + sim);
    }
}
