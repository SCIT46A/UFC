package app.scit46.ufc.dto.reward;

import java.util.List;
import java.util.stream.Collectors;

import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.entity.reward.RewardEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RewardDTO {
    private Long rewardId;
    private String rewardName;
    private Integer amount;
    private CampaignDTO campaign; // ✅ CampaignDTO 포함
    private List<RewardItemDTO> rewardItems; // ✅ ItemDTO 포함
    private List<RewardMaterialDTO> rewardMaterials; // ✅ RewardMaterialDTO 포함
    // 추가: RewardDeliveryDTO 목록 필드
    private List<RewardDeliveryDTO> rewardDeliveries; // ✅ RewardDeliveryDTO 포함

    public static RewardDTO toDTO(RewardEntity entity) {
        if (entity == null) return null;
        return RewardDTO.builder()
                .rewardId(entity.getRewardId())
                .rewardName(entity.getRewardName())
                .amount(entity.getAmount())
                .campaign(entity.getCampaign() != null ?
                        CampaignDTO.builder().campaignId(entity.getCampaign().getCampaignId()).build() : null)
                .rewardItems(entity.getRewardItems().stream()
                        .map(RewardItemDTO::toDTO)
                        .collect(Collectors.toList())) // ✅ ItemDTO 변환
                .rewardMaterials(entity.getRewardMaterials().stream()
                        .map(RewardMaterialDTO::toDTO)
                        .collect(Collectors.toList())) // ✅ RewardMaterialDTO 변환
                .rewardDeliveries(entity.getRewardDeliveries().stream()
                        .map(RewardDeliveryDTO::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
