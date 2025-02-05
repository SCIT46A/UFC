package app.scit46.ufc.entity;

import app.scit46.ufc.dto.CampaignBoardDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "CampaignBoards")
public class CampaignBoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_board_id")
    private Long cBoardId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private CampaignEntity campaign;

    // OneToMany: CampaignBoardReply.c_board_id 참조
    @OneToMany(mappedBy = "campaignBoard", fetch = FetchType.LAZY)
    private List<CampaignBoardReplyEntity> replies;

    public static CampaignBoardEntity toEntity(CampaignBoardDTO dto, CampaignEntity campaign) {
        return CampaignBoardEntity.builder()
                .cBoardId(dto.getCBoardId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdDate(dto.getCreatedDate())
                .campaign(campaign)
                .build();
    }
}
