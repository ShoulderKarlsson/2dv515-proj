package main.lib;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class Filehandler {


//    public ArrayList<Rating> getRatingsContent() {
//        ArrayList<Rating> ratings = new ArrayList<>();
//        try {
//            File r = ResourceUtils.getFile("classpath:ratings.csv");
//            Files.lines(r.toPath())
//                    .skip(1)
//                    .map(line -> line.split(","))
//                    .forEach(ratingInfo -> {
//                        int userId = 0;
//                        String movie = 0;
//                        double score = 0;
//                    });
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        return ratings;
//    }
//
//    public static String[] getMoviesContent() {
//        try {
//            File movies = ResourceUtils.getFile("classpath:movies.csv");
////
////            Files.lines(movies.toPath())
////                    .
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
}
