package fq.fretquiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    public static Instant nowMillis() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }

    public static <T> T randomElem(List<T> list) {
        Random random = ThreadLocalRandom.current();
        return randomElem(random, list);
    }

    public static <T> T randomElem(Random random, List<T> list) {
        int index = random.nextInt(list.size());
        return list.get(index);
    }
}
