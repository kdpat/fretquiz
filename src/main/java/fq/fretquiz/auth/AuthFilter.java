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

@Component
@Order(value = Integer.MIN_VALUE)
public class AuthFilter extends OncePerRequestFilter {

    private final UserService userService;

    public AuthFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        boolean isPageRequest = uri.equals("/") || uri.startsWith("/game");

        if (isPageRequest) {
            Cookie[] cookies = request.getCookies();

            Auth.findUserIdToken(cookies)
                    .flatMap(Auth::decodeUserIdToken)
                    .ifPresentOrElse(userId -> {
                        userService.findUser(userId)
                                .ifPresentOrElse(
                                        user -> request.setAttribute("user", user),
                                        () -> createUser(request, response));
                    }, () -> createUser(request, response));
        }

        filterChain.doFilter(request, response);
    }

    private void createUser(HttpServletRequest req, HttpServletResponse resp) {
        User user = userService.createUser();
        req.setAttribute("user", user);
        Cookie cookie = Auth.createUserCookie(user);
        resp.addCookie(cookie);
    }
}
