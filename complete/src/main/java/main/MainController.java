package main;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import main.lib.Recommender;
import main.lib.Something;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {
    HashSet<String> users = null;

    @RequestMapping("/users")
    @ResponseBody
    public HashSet<String> getUsers() {
        if (users == null) {
            generateUsers();
        }
        return users;
    }

    @RequestMapping(value = "/rec/{user}")
    public HashMap<String, HashMap<String, Double>> getRec(@PathVariable String user) {
        if (users == null || users.size() == 0) {
            generateUsers();
        }

        if (!user.contains(user)) {
            return null;
        }

        Recommender r = new Recommender();
        HashMap<String, HashMap<String, Double>> something = r.getRecomendationFor(user);
        return something;
    }

    private void generateUsers() {
        users = new HashSet<>();
        try {
            Files.lines(Paths.get("/home/shoulder/Documents/school/ai-2dv515/spring-template/complete/src/main/resources/x_ratings.csv"))
                    .skip(1)
                    .forEach(line -> users.add("User" + line.split(",")[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}