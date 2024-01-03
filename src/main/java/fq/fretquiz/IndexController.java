package fq.fretquiz;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
public class IndexController {

    @GetMapping(value = "/")
    public String showIndex() {
        return "index";
    }
}
