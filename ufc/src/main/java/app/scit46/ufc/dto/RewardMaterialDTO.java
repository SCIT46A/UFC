package app.scit46.ufc.dto;

import app.scit46.ufc.entity.RewardMaterialEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RewardMaterialDTO {
    private Long reMaterId;
    private Long rewardId;
    private Long materialId;
    private Integer quantityRequired;

    public static RewardMaterialDTO toDTO(RewardMaterialEntity entity) {
        return RewardMaterialDTO.builder()
                .reMaterId(entity.getReMaterId())
                .rewardId(entity.getReward() != null ? entity.getReward().getRewardId() : null)
                .materialId(entity.getMaterial() != null ? entity.getMaterial().getMaterialId() : null)
                .quantityRequired(entity.getQuantityRequired())
                .build();
    }
}
