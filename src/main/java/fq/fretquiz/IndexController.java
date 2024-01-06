package fq.fretquiz;

import fq.fretquiz.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class IndexController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @GetMapping(value = "/")
    public String showIndex(HttpServletRequest request, Model model) {
        User user = (User) request.getAttribute("user");
        model.addAttribute("user", user);
        log.info("index user: {}", user);
        return "index";
    }
}
