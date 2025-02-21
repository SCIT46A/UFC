package app.scit46.ufc.dto.reward;

import app.scit46.ufc.dto.MaterialDTO;
import app.scit46.ufc.entity.reward.RewardMaterialEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RewardMaterialDTO {
    private Long reMaterId;
    private RewardDTO reward; // ✅ RewardDTO 포함
    private MaterialDTO material; // ✅ MaterialDTO 포함
    private Integer quantityRequired;

    public static RewardMaterialDTO toDTO(RewardMaterialEntity entity) {
        return RewardMaterialDTO.builder()
                .reMaterId(entity.getReMaterId())
                .reward(entity.getReward() != null ? RewardDTO.toDTO(entity.getReward()) : null) // ✅ RewardDTO 변환
                .material(entity.getMaterial() != null ? MaterialDTO.toDTO(entity.getMaterial()) : null) // ✅ MaterialDTO 변환
                .quantityRequired(entity.getQuantityRequired())
                .build();
    }
}