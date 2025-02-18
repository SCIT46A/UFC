package app.scit46.ufc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.scit46.ufc.dto.CampaignDTO;
import app.scit46.ufc.dto.custom.GenerateCampaignDTO;
import app.scit46.ufc.service.campaign.CampaignService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final CampaignService campaignService;

    @PostMapping("/campaign/create")    
    public ResponseEntity<Long> createCampaign(@RequestBody GenerateCampaignDTO campaign) {
        Long campaignId = null;
        try{
            campaignId = campaignService.createCampaign(campaign);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
        
        return ResponseEntity.ok(campaignId);
    }

    @PostMapping("/campaign/update/{id}")
    public ResponseEntity<Long> updateCampaign(@PathVariable Long id, @RequestBody GenerateCampaignDTO campaign){
        try{
            campaignService.editCampaign(id, campaign);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
        
        return ResponseEntity.badRequest().body(null);
    }
}
