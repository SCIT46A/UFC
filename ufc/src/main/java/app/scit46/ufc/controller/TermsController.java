package app.scit46.ufc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/terms")
public class TermsController {

    @GetMapping("/banking-terms")
    public String banking() {
        return "/terms/e-banking-terms";
    }

    @GetMapping("/email-terms")
    public String email() {
        return "/terms/email-terms";
    }

    @GetMapping("/privacyprovision-terms")
    public String privacyprovision() {
        return "/terms/privacyprovision-terms";
    }

    @GetMapping("/refund-terms")
    public String refund() {
        return "/terms/refund-terms";
    }

    @GetMapping("/service-terms")
    public String service() {
        return "/terms/service-terms";
    }

    @GetMapping("/login-terms")
    public String login() {
        return "/terms/login-terms";
    }

    @GetMapping("/marketing-terms")
    public String marketing() {
        return "/terms/marketing-terms";
    }

}
