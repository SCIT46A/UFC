package app.scit46.ufc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

  @GetMapping("/")
  public String index() {
    return "index";
  }

  @GetMapping("/info")
  public String info() {
    return "upda/info";
  }
}
