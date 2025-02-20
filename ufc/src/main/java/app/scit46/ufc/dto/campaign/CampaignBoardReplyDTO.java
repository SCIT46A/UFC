package app.scit46.ufc.dto.campaign;

import java.time.LocalDateTime;

import app.scit46.ufc.entity.campaign.CampaignBoardReplyEntity;
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
public class CampaignBoardReplyDTO {
    private Long cBReplyId;
    private String content;
    private LocalDateTime createdDate;
    private Long cBoardId;
    private Long replyedById;

    public static CampaignBoardReplyDTO toDTO(CampaignBoardReplyEntity entity) {
        return CampaignBoardReplyDTO.builder()
                .cBReplyId(entity.getCBReplyId())
                .content(entity.getContent())
                .createdDate(entity.getCreatedDate())
                .cBoardId(entity.getCampaignBoard() != null ? entity.getCampaignBoard().getCBoardId() : null)
                .replyedById(entity.getReplyedBy() != null ? entity.getReplyedBy().getUserId() : null)
                .build();
    }
}
