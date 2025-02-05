package app.scit46.ufc.entity;

import app.scit46.ufc.dto.CampaignTagDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "CampaignTags")
public class CampaignTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_tag_id")
    private Long cTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private CampaignEntity campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private TagEntity tag;

    public static CampaignTagEntity toEntity(CampaignTagDTO dto, CampaignEntity campaign, TagEntity tag) {
        return CampaignTagEntity.builder()
                .cTagId(dto.getCTagId())
                .campaign(campaign)
                .tag(tag)
                .build();
    }
}
