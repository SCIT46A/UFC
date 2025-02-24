package app.scit46.ufc.dto.campaign;

import app.scit46.ufc.dto.TagDTO;
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
    private CampaignDTO campaign; // ✅ CampaignDTO 포함
    private TagDTO tag; // ✅ TagDTO 포함

    public static CampaignTagDTO toDTO(CampaignTagEntity entity) {
        return CampaignTagDTO.builder()
                .cTagId(entity.getCTagId())
                .campaign(entity.getCampaign() != null ? CampaignDTO.toDTO(entity.getCampaign()) : null) // ✅ CampaignDTO 변환
                .tag(entity.getTag() != null ? TagDTO.toDTO(entity.getTag()) : null) // ✅ TagDTO 변환
                .build();
    }
}
