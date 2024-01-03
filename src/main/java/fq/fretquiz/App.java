package fq.fretquiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	public static Instant nowMillis() {
		return Instant.now().truncatedTo(ChronoUnit.MILLIS)	;
	}
}
