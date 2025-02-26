package app.scit46.ufc.service.material;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.MaterialDTO;
import app.scit46.ufc.entity.MaterialEntity;
import app.scit46.ufc.repository.material.MaterialRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialEntity addMaterial(MaterialDTO materialDTO) {
        // 기존 재료가 있는지 먼저 확인
        return materialRepository.findByName(materialDTO.getName())
            .orElseGet(() -> {
                // 없으면 새로 저장
                MaterialEntity material = MaterialEntity.builder()
                    .name(materialDTO.getName())
                    .build();
                return materialRepository.save(material);
            });
    }

    // public void addRewardMaterial(Long campaignId, Long materialId, Integer amount) {
    //     RewardMaterialEntity rewardMaterial = RewardMaterialEntity.builder()
    //             .campaign(CampaignEntity.builder().campaignId(campaignId).build())
    //             .material(material)
    //             .amount(fundingItem.getAmount())
    //             .build();
    //     campaignMaterialRepository.save(campaignMaterial);
    // }
    
}
