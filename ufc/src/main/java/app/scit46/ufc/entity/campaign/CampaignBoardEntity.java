package app.scit46.ufc.entity.campaign;

import java.time.LocalDateTime;
import java.util.List;

import app.scit46.ufc.dto.campaign.CampaignBoardDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

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

    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private CampaignEntity campaign;

    // OneToMany: CampaignBoardReply.c_board_id 참조
    @OneToMany(mappedBy = "campaignBoard", fetch = FetchType.LAZY)
    private List<CampaignBoardReplyEntity> replies;

    public static CampaignBoardEntity toEntity(CampaignBoardDTO dto) {
        return CampaignBoardEntity.builder()
                .cBoardId(dto.getCBoardId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdDate(dto.getCreatedDate())
                .campaign(CampaignEntity.builder().campaignId(dto.getCampaign().getCampaignId()).build())
                .build();
    }
}
