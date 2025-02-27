package app.scit46.ufc.entity.campaign;

import java.time.LocalDateTime;

import app.scit46.ufc.dto.campaign.CampaignBoardReplyDTO;
import app.scit46.ufc.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "CampaignBoardReply")
public class CampaignBoardReplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_b_reply_id")
    private Long cBReplyId;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_board_id", nullable = false)
    private CampaignBoardEntity campaignBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replyed_by", nullable = false)
    private UserEntity replyedBy;

    public static CampaignBoardReplyEntity toEntity(CampaignBoardReplyDTO dto) {
        return CampaignBoardReplyEntity.builder()
                .cBReplyId(dto.getCBReplyId())
                .content(dto.getContent())
                .createdDate(dto.getCreatedDate())
                .campaignBoard(CampaignBoardEntity.builder().cBoardId(dto.getCampaignBoard().getCBoardId()).build())
                .replyedBy(UserEntity.builder().userId(dto.getReplyedBy().getUserId()).build())
                .build();
    }
}
