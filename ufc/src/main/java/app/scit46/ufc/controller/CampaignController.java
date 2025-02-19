package app.scit46.ufc.controller;

import app.scit46.ufc.dto.CampaignDTO;
import app.scit46.ufc.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/campaign")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @GetMapping("/all")
    public String allCampaign() {
        return "campaign/all-campaign";
    }

    @GetMapping("/expected")
    public String expectedCampaign() {
        return "campaign/expected-campaign";
    }

    @GetMapping("/detail")
    public String detailCampaign() {
        return "campaign/detail-campaign";
    }

    @GetMapping("/pay")
    public String payCampaign() {
        return "campaign/pay-campaign";
    }

    @GetMapping("/intro")
    public String intro() {
        return "campaign/intro-campaign";
    }

    @GetMapping("/create")
    public String create() {
        return "campaign/create-campaign";
    }

    @GetMapping("/active")
    public String active() {
        return "campaign/active-campaign";
    }

    @GetMapping("/upcoming")
    public String upcoming() {
        return "campaign/upcoming-campaign";
    }


}

