package app.scit46.ufc.entity;

import app.scit46.ufc.dto.CampaignBoardReplyDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    public static CampaignBoardReplyEntity toEntity(CampaignBoardReplyDTO dto, CampaignBoardEntity campaignBoard, UserEntity replyedBy) {
        return CampaignBoardReplyEntity.builder()
                .cBReplyId(dto.getCBReplyId())
                .content(dto.getContent())
                .createdDate(dto.getCreatedDate())
                .campaignBoard(campaignBoard)
                .replyedBy(replyedBy)
                .build();
    }
}
