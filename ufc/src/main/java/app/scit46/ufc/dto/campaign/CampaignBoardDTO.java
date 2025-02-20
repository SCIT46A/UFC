package app.scit46.ufc.dto.campaign;

import java.time.LocalDateTime;

import app.scit46.ufc.entity.campaign.CampaignBoardEntity;
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
public class CampaignBoardDTO {
    private Long cBoardId;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private Long campaignId;

    public static CampaignBoardDTO toDTO(CampaignBoardEntity entity) {
        return CampaignBoardDTO.builder()
                .cBoardId(entity.getCBoardId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .createdDate(entity.getCreatedDate())
                .campaignId(entity.getCampaign() != null ? entity.getCampaign().getCampaignId() : null)
                .build();
    }
}
