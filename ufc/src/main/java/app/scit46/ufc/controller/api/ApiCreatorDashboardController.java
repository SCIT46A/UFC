package app.scit46.ufc.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.service.campaign.CampaignService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiCreatorDashboardController {

    private final CampaignService campaignService;

}
