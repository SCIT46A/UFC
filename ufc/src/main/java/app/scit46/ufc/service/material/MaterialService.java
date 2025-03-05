package app.scit46.ufc.service.material;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.custom.GenerateCampaignDTO.RewardListDTO;
import app.scit46.ufc.entity.MaterialEntity;
import app.scit46.ufc.repository.material.MaterialRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    public List<Long> addMaterial(List<RewardListDTO> fundingItems) {
        List<Long> materialIds = new ArrayList<>();
        for (RewardListDTO fundingItem : fundingItems) {
            MaterialEntity material = MaterialEntity.builder()
                    .name(fundingItem.getName())
                    .build();
            Long materialId = materialRepository.save(material).getMaterialId();
        }
        return null;
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
