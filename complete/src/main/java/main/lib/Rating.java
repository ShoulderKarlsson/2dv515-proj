package main.lib;

class Rating {
    String movie;
    double score;

    Rating(String movie, double score) {
        this.movie = movie;
        this.score = score;
    }
    public boolean matches(Rating bRating) {
        return bRating.movie.equals(this.movie) && this.score == bRating.score;
    }
}