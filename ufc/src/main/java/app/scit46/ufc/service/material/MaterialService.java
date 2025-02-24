package app.scit46.ufc.service.material;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.MaterialDTO;
import app.scit46.ufc.dto.custom.FundingDTO;
import app.scit46.ufc.dto.custom.RewardListDTO;
import app.scit46.ufc.entity.MaterialEntity;
import app.scit46.ufc.repository.material.MaterialRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialEntity addMaterial(MaterialDTO fundingItem) {
        MaterialEntity material = MaterialEntity.builder()
                .name(fundingItem.getName())
                .build();
        return materialRepository.save(material);
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
