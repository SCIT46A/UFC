package app.scit46.ufc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/campaign")
public class CampaignController {

    @GetMapping("/all")
    public String allCampaign() {
        return "/campaign/all-campaign";
    }

    @GetMapping("/detail")
    public String detailCampaign() {
        return "/campaign/detail-campaign";
    }

    @GetMapping("/pay")
    public String payCampaign() {
        return "/campaign/pay-campaign";
    }

    @GetMapping("/intro")
    public String intro() {
        return "/campaign/intro-campaign";
    }

    @GetMapping("/create")
    public String create() {
        return "/campaign/create-campaign";
    }
}
