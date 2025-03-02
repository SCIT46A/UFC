package app.scit46.ufc.service.reward;

import org.springframework.stereotype.Service;
import app.scit46.ufc.dto.reward.RewardDeliveryDTO;
import app.scit46.ufc.repository.reward.RewardDeliveryRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardDeliveryService {

    private final RewardDeliveryRepository rewardDeliveryRepository;

    public List<RewardDeliveryDTO> findByCampaign_CampaignIdInAndStatus(List<Long> campaignIds, String status) {
        return rewardDeliveryRepository.findByCampaign_CampaignIdInAndStatus(campaignIds, status)
                .stream()
                .map(RewardDeliveryDTO::toDTO)
                .collect(Collectors.toList());
    }

}
