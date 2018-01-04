package main.lib;

public class Result {
    String name;
    double score;

    Result(String name, double score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public String toString() {
        return this.name + " - (" + this.score + ")";
    }
}
