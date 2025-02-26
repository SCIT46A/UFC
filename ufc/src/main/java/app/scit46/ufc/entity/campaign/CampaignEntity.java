package app.scit46.ufc.entity.campaign;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;

import app.scit46.ufc.dto.ImageUrlDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.entity.AlertTargetEntity;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.ImageUrlEntity;
import app.scit46.ufc.entity.MaterialDonationEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.entity.reward.RewardDeliveryEntity;
import app.scit46.ufc.entity.reward.RewardEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @Column(name = "campaign_status", nullable = false)
    private Boolean campaignStatus;

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


    public static CampaignEntity toEntity(CampaignDTO dto) {
      return CampaignEntity.builder()
              //.campaignId(dto.getCampaignId())  // 기본값 자동 생성이므로 주석처리
              .title(dto.getTitle())
              .description(dto.getDescription())
              .startDate(dto.getStartDate())
              .endDate(dto.getEndDate())
              .sendDate(dto.getSendDate())
              .createdDate(dto.getCreatedDate())
              //.createdDate(dto.getCreatedDate())  // 기본값 자동 생성이므로 주석처리
              //.createdBy(CreatorEntity.builder().ownUser(UserEntity.builder().userId(createdBy).build()).build()) // 영속성 문제로 인한 창작자 아이디 자체 설정
              .isSuccess(dto.getIsSuccess() == null ? false : dto.getIsSuccess())
              //.photo(ImageUrlEntity.builder().imageId(photo).build()) // 영속성 문제로 인한 이미지 아이디 자체 설정
              .campaignStatus(dto.getCampaignStatus() == null ? false : dto.getCampaignStatus())
              .rewards(dto.getRewards() != null ? dto.getRewards().stream()
                      .map(RewardEntity::toEntity)
                      .collect(Collectors.toList()) : null)
              .campaignTags(dto.getCampaignTags() != null ? dto.getCampaignTags().stream()
                      .map(CampaignTagEntity::toEntity)
                      .collect(Collectors.toList()) : null)
              .build();
  }

    // public static CampaignEntity toEntity(CampaignDTO dto, ImageUrlDTO photo) {
    //     return CampaignEntity.builder()
    //             //.campaignId(dto.getCampaignId())  // 기본값 자동 생성이므로 주석처리
    //             .title(dto.getTitle())
    //             .description(dto.getDescription())
    //             .startDate(dto.getStartDate())
    //             .endDate(dto.getEndDate())
    //             .sendDate(dto.getSendDate())
    //             //.createdDate(dto.getCreatedDate())  // 기본값 자동 생성이므로 주석처리
    //             //.createdBy(CreatorEntity.builder().ownUser(UserEntity.builder().userId(createdBy).build()).build()) // 영속성 문제로 인한 창작자 아이디 자체 설정
    //             .isSuccess(dto.getIsSuccess() == null ? false : dto.getIsSuccess())
    //             //.photo(ImageUrlEntity.builder().imageId(photo.getImageId()).build()) // 영속성 문제로 인한 이미지 아이디 자체 설정
    //             .campaignStatus(dto.getCampaignStatus() == null ? false : dto.getCampaignStatus())
    //             .build();
    // }
}
