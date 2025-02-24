package app.scit46.ufc.dto.reward;

import app.scit46.ufc.dto.ItemDTO;
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
    private CampaignDTO campaign; // ✅ CampaignDTO 포함
    private ItemDTO item; // ✅ ItemDTO 포함

    public static RewardDTO toDTO(RewardEntity entity) {
        return RewardDTO.builder()
                .rewardId(entity.getRewardId())
                .campaign(entity.getCampaign() != null ? CampaignDTO.toDTO(entity.getCampaign()) : null) // ✅ CampaignDTO 변환
                .item(entity.getItem() != null ? ItemDTO.toDTO(entity.getItem()) : null) // ✅ ItemDTO 변환
                .build();
    }
}
