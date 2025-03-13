package app.scit46.ufc.service.material;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.reward.RewardMaterialDTO;
import app.scit46.ufc.entity.reward.RewardMaterialEntity;
import app.scit46.ufc.repository.material.RewardMaterialRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RewardMaterialService {

    private final RewardMaterialRepository rewardMaterialRepository;
    
    public RewardMaterialEntity addRewardMaterial(RewardMaterialEntity rewardMaterial) {
        return rewardMaterialRepository.save(rewardMaterial);
    }
}
