package main;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

import main.lib.Recommender;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {
    HashSet<String> users = null;

    @RequestMapping("/users")
    @ResponseBody
    public HashSet<String> getUsers() {
        if (users == null) { generateUsers(); }
        return users;
    }

    @RequestMapping(value = "/rec/{user}")
    public void getRec(@PathVariable String user) {
        if (users == null || users.size() == 0) {
            generateUsers();
        }

        if (!user.contains(user)) { return; }

        Recommender r = new Recommender();
        Object something = r.getRecomendationFor(user);
    }

    private void generateUsers() {
        users = new HashSet<>();
        try {
            Files.lines(Paths.get("/home/shoulder/Documents/school/ai-2dv515/spring-template/complete/src/main/resources/ratings.csv"))
                    .skip(1)
                    .forEach(line -> users.add("User" + line.split(",")[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}