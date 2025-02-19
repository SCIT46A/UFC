package app.scit46.ufc.dto;

import app.scit46.ufc.entity.CampaignGoalEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CampaignGoalDTO {
    private Long goalId;
    private Long campaignId;
    private Long materialId;
    private Integer quantityRequired;

    public static CampaignGoalDTO toDTO(CampaignGoalEntity entity) {
        return CampaignGoalDTO.builder()
                .goalId(entity.getGoalId())
                .campaignId(entity.getCampaign() != null ? entity.getCampaign().getCampaignId() : null)
                .materialId(entity.getMaterial() != null ? entity.getMaterial().getMaterialId() : null)
                .quantityRequired(entity.getQuantityRequired())
                .build();
    }
}
