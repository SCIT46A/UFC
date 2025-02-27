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
        return campaignGoalRepository.findByCampaign_CampaignId(campaignId);
        // 레포지토리 단에서부터 DTO 리스트를 반환하게 되어있어 밑의 코드는 필요 없음
                // .stream()
                // .map(CampaignGoalDTO::toDTO)
                // .collect(Collectors.toList());
    }

}