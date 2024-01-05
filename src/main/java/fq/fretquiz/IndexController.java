package fq.fretquiz;

import fq.fretquiz.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/")
    public String showIndex(HttpServletRequest request, Model model) {
        var user = userService.fetchUserFromRequest(request).orElse(null);
        model.addAttribute("user", user);
        log.info("found user: {}", user);
        return "index";
    }
}
