package app.scit46.ufc.dto.campaign;

import app.scit46.ufc.dto.MaterialDTO;
import app.scit46.ufc.entity.campaign.CampaignGoalEntity;
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
public class CampaignGoalDTO {
    private Long goalId;
    private CampaignDTO campaign; // ✅ CampaignDTO 포함
    private MaterialDTO material; // ✅ MaterialDTO 포함
    private Integer quantityRequired;

    public static CampaignGoalDTO toDTO(CampaignGoalEntity entity) {
        return CampaignGoalDTO.builder()
                .goalId(entity.getGoalId())
                .campaign(entity.getCampaign() != null ? CampaignDTO.toDTO(entity.getCampaign()) : null) // ✅ CampaignDTO 변환
                .material(entity.getMaterial() != null ? MaterialDTO.toDTO(entity.getMaterial()) : null) // ✅ MaterialDTO 변환
                .quantityRequired(entity.getQuantityRequired())
                .build();
    }
}
