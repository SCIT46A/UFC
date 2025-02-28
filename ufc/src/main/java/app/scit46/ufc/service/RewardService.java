package app.scit46.ufc.service;

import app.scit46.ufc.dto.reward.RewardDTO;
import app.scit46.ufc.entity.reward.RewardEntity;
import app.scit46.ufc.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardService {

    final private RewardRepository RewardRepository;

    public List<RewardDTO> getRewards(Long campaignId) {
        // 예시: RewardEntity 리스트를 조회 후 DTO로 변환
        List<RewardEntity> rewardEntities = RewardRepository.findAllByCampaign_CampaignId(campaignId);
        return rewardEntities.stream()
                .map(RewardDTO::toDTO)
                .collect(Collectors.toList());
    }


}
