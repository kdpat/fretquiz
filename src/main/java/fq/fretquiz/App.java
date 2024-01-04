package fq.fretquiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

	public static final String ENCODE_CHARS = "bcdfghjklmnpqrstvwxzBCDFGHJKLMNPQRSTVWXZ25679";
	public static final int ENCODE_CHARS_LEN = ENCODE_CHARS.length();

	// adapted from 3cats-in-a-coat comment
	// https://news.ycombinator.com/item?id=38414914
	public static String encodeId(Long id) {
		if (id < 0) {
			throw new IllegalArgumentException("id must be a positive number");
		}

		var stringBuilder = new StringBuilder();

		while (id > 0) {
			var nextIndex = (int) (id % ENCODE_CHARS_LEN);
			var nextChar = ENCODE_CHARS.charAt(nextIndex);
			stringBuilder.append(nextChar);
			id /= ENCODE_CHARS_LEN;
		}

		return stringBuilder.reverse().toString();
	}


	public static Long decodeId(String encodedId) {
		var id = 0L;
		var multiplier = 1L;

		for (int i = encodedId.length() - 1; i >= 0; i--) {
			var nextChar = encodedId.charAt(i);
			var nextIndex = ENCODE_CHARS.indexOf(nextChar);
			id += (nextIndex * multiplier);
			multiplier *= ENCODE_CHARS_LEN;
		}

		return id;
	}
}
