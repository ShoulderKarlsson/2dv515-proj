package main;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import main.lib.Recommender;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {
    HashSet<String> users = null;

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping("/users")
    @ResponseBody
    public HashSet<String> getUsers() {
        if (users == null) {
            generateUsers();
        }
        return users;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(value = "/rec/{user}")
    public HashMap<String, HashMap<String, Double>> getRec(@PathVariable String user) {
        if (users == null || users.size() == 0) {
            generateUsers();
        }

        if (!user.contains(user)) {
            return null;
        }

        Recommender r = new Recommender();
        return r.getRecomendationFor(user);
    }

    private void generateUsers() {
        users = new HashSet<>();
        try {
            File ratings = ResourceUtils.getFile("classpath:ratings.csv");

            Files.lines(ratings.toPath())
                    .skip(1)
                    .forEach(line -> users.add("User" + line.split(",")[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}