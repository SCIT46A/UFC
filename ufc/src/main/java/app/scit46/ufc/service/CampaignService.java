package app.scit46.ufc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import app.scit46.ufc.entity.CampaignEntity;
import app.scit46.ufc.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;
    // private final CampaignRepository campaignRepository;

    public List<CampaignEntity> campaignFindByCampaignId(Long campaignId) {
        return campaignRepository.findByCampaignId(campaignId);
    }

}
