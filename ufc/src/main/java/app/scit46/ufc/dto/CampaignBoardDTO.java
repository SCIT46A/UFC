package app.scit46.ufc.dto;

import app.scit46.ufc.entity.CampaignBoardEntity;
import lombok.*;
import java.time.LocalDateTime;

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
