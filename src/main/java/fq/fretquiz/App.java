package fq.fretquiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.sqids.Sqids;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
		var random = ThreadLocalRandom.current();
		var index = random.nextInt(list.size());
		return list.get(index);
	}

	@Bean
	public Sqids sqids() {
		return Sqids.builder().build();
	}
}
