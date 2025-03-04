package app.scit46.ufc.dto.campaign;

import java.time.LocalDateTime;

import app.scit46.ufc.dto.UserDTO;
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
    private CampaignBoardDTO campaignBoard; // ✅ CampaignBoardDTO 포함
    private UserDTO replyedBy; // ✅ UserDTO 포함

    public static CampaignBoardReplyDTO toDTO(CampaignBoardReplyEntity entity) {
        return CampaignBoardReplyDTO.builder()
                .cBReplyId(entity.getCBReplyId())
                .content(entity.getContent())
                .createdDate(entity.getCreatedDate())
                .campaignBoard(entity.getCampaignBoard() != null ? CampaignBoardDTO.toDTO(entity.getCampaignBoard()) : null) // ✅ CampaignBoardDTO 변환
                .replyedBy(entity.getReplyedBy() != null ? UserDTO.toDTO(entity.getReplyedBy()) : null) // ✅ UserDTO 변환
                .build();
    }
}
