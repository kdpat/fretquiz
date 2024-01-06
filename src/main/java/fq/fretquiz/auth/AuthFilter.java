package fq.fretquiz.auth;

import fq.fretquiz.user.User;
import fq.fretquiz.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@Order(value = Integer.MIN_VALUE)
public class AuthFilter extends OncePerRequestFilter {

    /**
     * The pages that expect a user attr to be set.
     * We set this so that we can ignore requests to js/css/image/other files.
     */
    public static final Set<String> USER_ATTR_URIS = Set.of("/", "/game");

    private final UserService userService;

    public AuthFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req,
                                    @NonNull HttpServletResponse resp,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String uri = req.getRequestURI();

        if (USER_ATTR_URIS.contains(uri)) {
            Cookie[] cookies = req.getCookies();

            var user = Auth.findUserIdToken(cookies)
                    .flatMap(Auth::decodeUserIdToken)
                    .flatMap(userService::findUser)
                    .orElseGet(() -> {
                        User u = userService.createUser();
                        Cookie cookie = Auth.createUserCookie(u);
                        resp.addCookie(cookie);
                        return u;
                    });

            req.setAttribute("user", user);
        }
        filterChain.doFilter(req, resp);
    }
}
