package app.scit46.ufc.entity.reward;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import app.scit46.ufc.dto.reward.RewardDTO;
import app.scit46.ufc.entity.ItemEntity;
import app.scit46.ufc.entity.campaign.CampaignEntity;
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
@Table(name = "Rewards")
public class RewardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_id")
    private Long rewardId;

    @Column(name = "reward_name", nullable = false, length = 100)
    private String rewardName;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "campaign_id", nullable = false)
    private CampaignEntity campaign;

    // OneToMany: RewardItems.reward 참조
    @OneToMany(mappedBy = "reward", fetch = FetchType.LAZY)
    private List<RewardItemEntity> rewardItems;

    // OneToMany: RewardMaterials.reward 참조
    @OneToMany(mappedBy = "reward", fetch = FetchType.LAZY)
    private List<RewardMaterialEntity> rewardMaterials;

    public static RewardEntity toEntity(RewardDTO dto) {
        return RewardEntity.builder()
                .rewardId(dto.getRewardId())
                .amount(dto.getAmount())
                .campaign(CampaignEntity.toEntity(dto.getCampaign()))
                .rewardItems(dto.getRewardItems().stream()
                        .map(RewardItemEntity::toEntity)
                        .collect(Collectors.toList()))
                .rewardMaterials(dto.getRewardMaterials().stream()
                        .map(RewardMaterialEntity::toEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
