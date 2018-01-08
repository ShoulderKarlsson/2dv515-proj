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

//                        u.addRating(new Rating(this.movies.get(movieId), rating));
                        u.add_rating(this.movies.get(movieId), rating);
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
            userRatings
                    .keySet()
                    .stream()
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
                                        
                                    }
                                }
                            });
                });


        /**
         * What works?
         * I can calculate the total weighted score of a movie
         * I can calculate each users specific weighted score for all movies rated
         * I can caluclate the sum of all similiarities for a movie
         */
        // Each users weighted scores for each movie that they have rated.
//        HashMap<User, HashMap<String, Double>> userWeightedScores = getWeightedScores(res);
//        HashMap<String, Double> movieTotalWeightedScore = getMovieTotalScore(userWeightedScores);
//        HashMap<String, Double> similaritySumForEachMovie = calculateSumSim(userWeightedScores);


//        HashMap<String, User> n_recs = new HashMap<>();
//        Iterator<User> it =  db.values().iterator();
//
//        while(it.hasNext()) {
//            User usr = it.next();
//
//            for (Rating r : usr.ratings) {
//                String item = r.movie;
//                double score = r.score;
//
//                if (!n_recs.containsKey(item)) {
//                    n_recs.put(item, new User(item));
//                }
//
//                User n_usr = n_recs.get(item);
//                n_usr.addRating(new Rating(usr.getUsername() + "sdf", score));
//            }
//        }
//
//        ArrayList<Result> itemRes = new ArrayList<>();
//        for (String k : n_recs.keySet()) {
//            User movieUser = n_recs.get(k);
//
//            itemRes.add(new Result(movieUser.getUsername(), calc_Euclidian(movieUser, user)));
//
//        }
//
//        System.out.println(itemRes);

        return null;
    }

//    private HashMap<String, Double> getMovieTotalScore(HashMap<User, HashMap<String, Double>> userWeightedScores) {
//        HashMap<String, Double> movieTotalScore = new HashMap<>();
//        for (User u : userWeightedScores.keySet()) {
//            HashMap<String, Double> userScores = userWeightedScores.get(u);
//            for (String movieName : userScores.keySet()) {
//                double movieScore = userScores.get(movieName);
//                if (movieTotalScore.containsKey(movieName)) {
//                    movieTotalScore.replace(movieName, movieTotalScore.get(movieName) + movieScore);
//                } else {
//                    movieTotalScore.put(movieName, movieScore);
//                }
//            }
//        }
//
//        return movieTotalScore;
//    }
//
//
//    /**
//     * Calculates each users weighted score for all the movies that the specific user has watched
//     */
//    HashMap<User, HashMap<String, Double>> getWeightedScores(ArrayList<Result> results) {
//
//        // Key - A user
//        // Hashmap with weighted scores for each movie for the specific user
//        HashMap<User, HashMap<String, Double>> weightedScores = new HashMap<>();
//        for (Result r : results) {
//            HashMap<String, Double> scores = new HashMap<>();
//            for (Rating rating : r.user.ratings) {
//                scores.put(rating.movie, r.simScore * rating.score);
//            }
//
//            weightedScores.put(r.user, scores);
//        }
//
//        return weightedScores;
//    }
//
//    HashMap<String, Double> calculateSumSim(HashMap<User, HashMap<String, Double>> scores) {
//        HashMap<String, Double> movieSumSimScores = new HashMap<>();
//        for (int movieId : movies.keySet()) {
//            String movieName = movies.get(movieId);
//            double movieSumSimScore = 0.0;
//            for (User u : scores.keySet()) {
//                HashMap<String, Double> userScores = scores.get(u);
//                if (userScores.containsKey(movieName)) {
//                    movieSumSimScore += userScores.get(movieName);
//                }
//            }
//            movieSumSimScores.put(movieName, movieSumSimScore);
//        }
//
//
//        return movieSumSimScores;
//    }


//    private double calc_Euclidian(User A, User B) {
//        double sim = 0.0;
//        int cnt_sim = 0;
//
//        for (Rating aRating : A.ratings) {
//            for (Rating bRating : B.ratings) {
//                // TODO: Redo matches? Not really sure the comparison is correct
////                if (aRating.matches(bRating)) {
//                    sim += Math.pow(aRating.score - bRating.score, 2);
//                    cnt_sim++;
////                }
//            }
//        }
//
//        return cnt_sim == 0 ? 0 : 1.0 / (1.0 + sim);
//    }

    private double _calc_Euclidian(User A, User B) {
        double sim = 0.0;
        int cnt_sim = 0;
        HashMap<String, Double> ARatings = A.get_ratings();
        HashMap<String, Double> BRatings = B.get_ratings();

        for (String AKey : A.get_ratings().keySet()) {
            for (String BKey : B.get_ratings().keySet()) {
                // TODO: ===========================================
                // TODO: There should be some sort of match here.. =
                // TODO: ===========================================
                sim += Math.pow(A.getRating(AKey) - B.getRating(BKey), 2);
                cnt_sim++;
            }
        }

        return cnt_sim == 0 ? 0 : 1.0 / (1.0 + sim);
    }
}
