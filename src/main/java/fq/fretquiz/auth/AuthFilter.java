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
     * This filter will ignore requests to any other uris.
     */
    public static final Set<String> USER_ATTR_URIS = Set.of("/", "/game");

    private final UserService userService;

    public AuthFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (USER_ATTR_URIS.contains(uri)) {
            Cookie[] cookies = request.getCookies();

            var user = Auth.decodeUserCookie(cookies)
                    .flatMap(userService::findUser)
                    .orElseGet(() -> createUserAndCookie(response));

            request.setAttribute("user", user);
        }
        filterChain.doFilter(request, response);
    }

    private User createUserAndCookie(HttpServletResponse response) {
        User user = userService.createUser();
        Cookie cookie = Auth.createUserCookie(user);
        response.addCookie(cookie);
        return user;
    }
}
