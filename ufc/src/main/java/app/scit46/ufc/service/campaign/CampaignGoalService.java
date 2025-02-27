package app.scit46.ufc.service.campaign;

import app.scit46.ufc.dto.campaign.CampaignGoalDTO;
import app.scit46.ufc.repository.CampaignGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignGoalService {

    private final CampaignGoalRepository campaignGoalRepository;

    public List<CampaignGoalDTO> findAll(Long campaignId) {
        return campaignGoalRepository.findByCampaign_CampaignId(campaignId)
                .stream()
                .map(CampaignGoalDTO::toDTO)
                .collect(Collectors.toList());
    }



}
