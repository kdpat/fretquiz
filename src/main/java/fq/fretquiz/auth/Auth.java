package fq.fretquiz.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import fq.fretquiz.App;
import fq.fretquiz.user.User;
import jakarta.servlet.http.Cookie;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;

public class Auth {

    private static final String USER_COOKIE = "_fq_user";

    private static final String ISSUER = "fq";
    private static final String SECRET = "_secret";
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);

    public static final JWTVerifier VERIFIER = JWT.require(ALGORITHM)
            .withIssuer(ISSUER)
            .build();

    public static String encodeUserIdToken(Long userId) {
        Instant now = App.nowMillis();

        return JWT.create()
                .withClaim("userId", userId)
                .withIssuer(ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(1, ChronoUnit.DAYS))
                .sign(ALGORITHM);
    }

    public static Optional<Long> decodeUserIdToken(String token) {
        try {
            DecodedJWT jwt = VERIFIER.verify(token);
            Long userId = jwt.getClaim("userId").asLong();
            return Optional.of(userId);
        } catch (Exception _e) {
            return Optional.empty();
        }
    }

    public static Optional<String> findUserIdToken(Cookie[] cookies) {
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(USER_COOKIE))
                .findFirst()
                .map(Cookie::getValue);
    }

    public static Cookie createUserCookie(User user) {
        String token = encodeUserIdToken(user.id());
        var cookie = new Cookie(USER_COOKIE, token);
        cookie.setAttribute("SameSite", "Lax");
        cookie.setPath("/");
        return cookie;
    }

    public static Optional<Long> decodeUserCookie(Cookie[] cookies) {
        return findUserIdToken(cookies)
                .flatMap(Auth::decodeUserIdToken);
    }
}
