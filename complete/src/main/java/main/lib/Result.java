package main.lib;

public class Result {
    User user;
    double simScore;

    Result(User user, double simScore) {
        this.user = user;
        this.simScore = simScore;
    }

    @Override
    public String toString() {
        return this.user.getUsername() + " - (" + this.simScore + ")";
    }
}
