package fq.fretquiz;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AppTest {

	Logger log = LoggerFactory.getLogger(AppTest.class);

	@Test
	void contextLoads() {
	}

	@Test
	void encodeId() {
		var id = 1234L;
		var encodedId = App.encodeId(id);
		log.info("encoded id: {}", encodedId);

		var decodedId = App.decodeId(encodedId);
		log.info("decoded id: {}", decodedId);
		assertEquals(id, decodedId);

		var random = ThreadLocalRandom.current();

		for (int i = 0; i < 100; i++) {
			var n = random.nextLong(1000000);
			var encoded = App.encodeId(n);
			var decoded = App.decodeId(encoded);
			log.info("n: {}, encoded: {}, decoded: {}", n, encoded, decoded);
			assertEquals(n, decoded);
		}
	}
}
