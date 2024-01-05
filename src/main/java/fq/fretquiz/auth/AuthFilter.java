package fq.fretquiz.auth;

import fq.fretquiz.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(value = Integer.MIN_VALUE)
public class AuthFilter extends OncePerRequestFilter {

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    public AuthFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        var uri = request.getRequestURI();
        var isPageRequest = uri.equals("/") || uri.startsWith("/game");

        if (isPageRequest) {
            var cookies = request.getCookies();
            var token = Auth.findUserIdToken(cookies).orElse(null);

            if (token == null) {
                addNewUserCookie(response);
            } else {
                var userId = Auth.decodeUserIdToken(token).orElse(null);

                if (userId == null || !userService.userExists(userId)) {
                    addNewUserCookie(response);
                } else {
                    log.info("user found: {}", userId);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void addNewUserCookie(HttpServletResponse response) {
        var user = userService.createUser();
        log.info("user created: {}", user);
        var cookie = Auth.createUserCookie(user);
        response.addCookie(cookie);
    }
}
