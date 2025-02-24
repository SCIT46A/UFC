package app.scit46.ufc.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.scit46.ufc.dto.custom.GenerateCampaignDTO;
import app.scit46.ufc.service.campaign.CampaignService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/campaign")
@RequiredArgsConstructor
public class ApiCampaignController {

    private final CampaignService campaignService;

    @PostMapping("/create")    
    public ResponseEntity<?> createCampaign(@RequestBody GenerateCampaignDTO campaign) {
        Long campaignId = campaignService.createCampaign(campaign);
        
        return ResponseEntity.ok(campaignId);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Long> updateCampaign(@PathVariable Long id, @RequestBody GenerateCampaignDTO campaign){
        
        campaignService.editCampaign(id, campaign);
        
        return ResponseEntity.badRequest().body(null);
    }
}
