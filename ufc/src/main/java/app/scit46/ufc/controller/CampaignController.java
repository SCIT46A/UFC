package app.scit46.ufc.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import app.scit46.ufc.dto.CampaignDTO;
import app.scit46.ufc.service.campaign.CampaignService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/campaign")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @GetMapping("/all")
    public String allCampaign(Model model, @RequestParam(defaultValue = "") String searchKeyword) {
        List<CampaignDTO> campaigns = campaignService.readCampaignList(searchKeyword);
        model.addAttribute("campaigns", campaigns);
        return "campaign/all-campaign";
    }

    @GetMapping("/{id}")
    public String detailCampaign(@PathVariable Long id, Model model) {
        CampaignDTO campaign = campaignService.readCampaign(id);
        model.addAttribute("campaign", campaign);
        return "campaign/detail-campaign";
    }

    @GetMapping("/pay")
    public String payCampaign(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername(); // getUserName() 을 따로 만들지 고민 필요(사용자 이름)
        model.addAttribute("username", username);
        return "campaign/pay-campaign";
    }

    @GetMapping("/intro")
    public String intro() {
        return "campaign/intro-campaign";
    }

    @GetMapping("/create") // 헤더에 존재하는 사용자 정보를 가져옴?
    public String create(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername(); // getUserName() 을 따로 만들지 고민 필요(사용자 이름)
        model.addAttribute("username", username);
        return "campaign/create-campaign";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id, Model model) {
        CampaignDTO campaign = campaignService.readCampaign(id);
        model.addAttribute("campaign", campaign);
        return "campaign/update-campaign";
    }
}
