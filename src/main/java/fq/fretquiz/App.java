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
        int index = random.nextInt(list.size());
		return list.get(index);
	}

	public static final String ENCODE_CHARS = "bcdfghjklmnpqrstvwxzBCDFGHJKLMNPQRSTVWXZ25679";
	public static final int ENCODE_CHARS_LEN = ENCODE_CHARS.length();

	// adapted from 3cats-in-a-coat comment
	// https://news.ycombinator.com/item?id=38414914
	public static String encodeId(Long id) {
		if (id < 0) {
			throw new IllegalArgumentException("id must be a positive number");
		}

        StringBuilder builder = new StringBuilder();

		while (id > 0) {
			int index = (int) (id % ENCODE_CHARS_LEN);
			char charAt = ENCODE_CHARS.charAt(index);
			builder.append(charAt);
			id /= ENCODE_CHARS_LEN;
		}

		return builder.reverse().toString();
	}

	public static Long decodeId(String encodedId) {
		long id = 0;
		long multiplier = 1;

		for (int i = encodedId.length() - 1; i >= 0; i--) {
			char charAt = encodedId.charAt(i);
			int index = ENCODE_CHARS.indexOf(charAt);
			id += (index * multiplier);
			multiplier *= ENCODE_CHARS_LEN;
		}

		return id;
	}
}
