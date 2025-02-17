package app.scit46.ufc.service;

import org.springframework.stereotype.Service;

import app.scit46.ufc.entity.CampaignEntity;
import app.scit46.ufc.entity.MaterialDonationEntity;
import app.scit46.ufc.repository.CampaignRepository;
import app.scit46.ufc.repository.MaterialDonationRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final MaterialDonationRepository materialDonationRepository;
    private final CampaignRepository campaignRepository;

    public CampaignEntity campaignFindByCampaignId(Long campaignId) {
        MaterialDonationEntity materialDonation = materialDonationRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
    return campaignRepository.findByDonation(materialDonation)
            .orElseThrow(() -> new RuntimeException("Campaign not found"));
        }

}
