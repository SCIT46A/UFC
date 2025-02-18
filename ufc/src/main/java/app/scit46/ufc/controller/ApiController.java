package app.scit46.ufc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.scit46.ufc.dto.custom.CreateCampaignDTO;
import app.scit46.ufc.service.campaign.CampaignService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final CampaignService campaignService;

    @PostMapping("/campaign/create")    
    public ResponseEntity<String> createCampaign(@RequestBody CreateCampaignDTO campaign) {
        try{
            campaignService.createCampaign(campaign);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Campaign creation failed");
        }
        return ResponseEntity.ok("Campaign created successfully");
    }
}
