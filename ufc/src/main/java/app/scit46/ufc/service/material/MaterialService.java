package app.scit46.ufc.service.material;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.MaterialDTO;
import app.scit46.ufc.dto.custom.FundingDTO;
import app.scit46.ufc.dto.custom.RewardListDTO;
import app.scit46.ufc.entity.MaterialEntity;
import app.scit46.ufc.repository.material.MaterialRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialEntity addMaterial(String name) {
        // 기존 재료가 있는지 먼저 확인
        return materialRepository.findByName(name)
            .orElseGet(() -> {
                // 없으면 새로 저장
                MaterialEntity material = new MaterialEntity();
                material.setName(name);
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
