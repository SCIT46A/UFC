package app.scit46.ufc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/terms")
public class TermsController {

    @GetMapping("/banking")
    public String banking() {
        return "terms/e-banking-terms";
    }

    @GetMapping("/email")
    public String email() {
        return "terms/email-terms";
    }

    @GetMapping("/privacyprovision")
    public String privacyprovision() {
        return "terms/privacyprovision-terms";
    }

    @GetMapping("/refund")
    public String refund() {
        return "terms/refund-terms";
    }

    @GetMapping("/service")
    public String service() {
        return "terms/service-terms";
    }

    @GetMapping("/login")
    public String login() {
        return "terms/login-terms";
    }

    @GetMapping("/marketing")
    public String marketing() {
        return "terms/marketing-terms";
    }

}
