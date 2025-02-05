package app.scit46.ufc.entity;

import app.scit46.ufc.dto.RewardDTO;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private CampaignEntity campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    // OneToMany: RewardMaterials.reward 참조
    @OneToMany(mappedBy = "reward", fetch = FetchType.LAZY)
    private List<RewardMaterialEntity> rewardMaterials;

    public static RewardEntity toEntity(RewardDTO dto, CampaignEntity campaign, ItemEntity item) {
        return RewardEntity.builder()
                .rewardId(dto.getRewardId())
                .campaign(campaign)
                .item(item)
                .build();
    }
}
