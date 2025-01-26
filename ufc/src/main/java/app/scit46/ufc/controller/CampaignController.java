package app.scit46.ufc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("campaign")
public class CampaignController {

    @GetMapping("all-campaign")
    public String allCampaign() {
        return "/campaign/all-campaign";
    }
}
