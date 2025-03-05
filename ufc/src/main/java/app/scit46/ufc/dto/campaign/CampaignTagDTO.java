package app.scit46.ufc.dto.campaign;

import app.scit46.ufc.entity.campaign.CampaignTagEntity;
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
public class CampaignTagDTO {
    private Long cTagId;
    private Long campaignId;
    private Integer tagId;

    public static CampaignTagDTO toDTO(CampaignTagEntity entity) {
        return CampaignTagDTO.builder()
                .cTagId(entity.getCTagId())
                .campaignId(entity.getCampaign() != null ? entity.getCampaign().getCampaignId() : null)
                .tagId(entity.getTag() != null ? entity.getTag().getTagId() : null)
                .build();
    }
}
