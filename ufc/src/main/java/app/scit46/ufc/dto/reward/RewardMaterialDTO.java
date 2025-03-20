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
        if (entity == null) return null;
        
        return RewardMaterialDTO.builder()
            .reMaterId(entity.getReMaterId())
            // 리워드는 ID만 포함
            .reward(entity.getReward() != null ? 
                RewardDTO.builder().rewardId(entity.getReward().getRewardId()).build() : null)
            // 재료는 기본 정보만 포함 (컬렉션 필드 제외)
            .material(entity.getMaterial() != null ? 
                MaterialDTO.builder()
                    .materialId(entity.getMaterial().getMaterialId())
                    .name(entity.getMaterial().getName())
                    // 중요: rewardMaterials 컬렉션 제외
                    .build() : null)
            .quantityRequired(entity.getQuantityRequired())
            .build();
    }
}