package app.scit46.ufc.entity;

import app.scit46.ufc.dto.CampaignDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Campaigns")
public class CampaignEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    private Long campaignId;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "send_date", nullable = false)
    private LocalDateTime sendDate;

    @Column(name = "created_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private CreatorEntity createdBy;

    @Column(name = "is_success", nullable = false)
    private Boolean isSuccess;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private ImageUrlEntity photo;

    // OneToMany relationships
    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private List<CampaignBoardEntity> boards;

    @OneToMany(mappedBy = "campaignedBy", fetch = FetchType.LAZY)
    private List<CampaignReviewEntity> reviews;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private List<CampaignGoalEntity> campaignGoals;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private List<MaterialDonationEntity> materialDonations;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private List<RewardEntity> rewards;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private List<CampaignTagEntity> campaignTags;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private List<RewardDeliveryEntity> rewardDeliveries;

    // OneToMany: AlertTarget.target_campaign 참조
    @OneToMany(mappedBy = "targetCampaign", fetch = FetchType.LAZY)
    private List<AlertTargetEntity> alertTargets;

    public static CampaignEntity toEntity(CampaignDTO dto, CreatorEntity createdBy, ImageUrlEntity photo) {
        return CampaignEntity.builder()
                //.campaignId(dto.getCampaignId())  // 기본키 자동 생성이므로 주석처리
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .sendDate(dto.getSendDate())
                //.createdDate(dto.getCreatedDate())  // 기본키 자동 생성이므로 주석처리
                .createdBy(createdBy)
                .isSuccess(dto.getIsSuccess())
                .photo(photo)
                .build();
    }
}
