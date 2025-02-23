package app.scit46.ufc.dto.reward;

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
    private Long campaignId;
    private Long itemId;

    public static RewardDTO toDTO(RewardEntity entity) {
        return RewardDTO.builder()
                .rewardId(entity.getRewardId())
                .campaignId(entity.getCampaign() != null ? entity.getCampaign().getCampaignId() : null)
                .itemId(entity.getItem() != null ? entity.getItem().getItemId() : null)
                .build();
    }
}
