package app.scit46.ufc.entity;

import app.scit46.ufc.dto.CampaignReviewDTO;
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
@Table(name = "CampaignReviews")
public class CampaignReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_review_id")
    private Long cReviewId;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "rated", nullable = false)
    private Double rated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by", nullable = false)
    private UserEntity reviewedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaigned_by", nullable = false)
    private CampaignEntity campaignedBy;

    public static CampaignReviewEntity toEntity(CampaignReviewDTO dto, UserEntity reviewedBy, CampaignEntity campaignedBy) {
        return CampaignReviewEntity.builder()
                .cReviewId(dto.getCReviewId())
                .content(dto.getContent())
                .createdDate(dto.getCreatedDate())
                .rated(dto.getRated())
                .reviewedBy(reviewedBy)
                .campaignedBy(campaignedBy)
                .build();
    }
}
