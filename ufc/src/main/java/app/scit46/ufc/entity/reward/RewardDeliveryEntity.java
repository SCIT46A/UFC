package app.scit46.ufc.entity.reward;

import app.scit46.ufc.dto.reward.RewardDeliveryDTO;
import app.scit46.ufc.entity.MaterialDonationEntity;
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
@Table(name = "RewardDeliveries")
public class RewardDeliveryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "r_delivery_id")
    private Long rDeliveryId;

    @Column(name = "invoice", length = 100)
    private String invoice;

    @Column(name = "status", length = 100)
    private String status;

    // 기존 CampaignEntity 대신 RewardEntity와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private RewardEntity reward;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id")
    private MaterialDonationEntity donation;

    // 추가된 amount 컬럼
    @Column(name = "amount", nullable = false)
    private Integer amount;

    public static RewardDeliveryEntity toEntity(RewardDeliveryDTO dto) {
        return RewardDeliveryEntity.builder()
                .rDeliveryId(dto.getRDeliveryId())
                .invoice(dto.getInvoice())
                .status(dto.getStatus())
                .reward(RewardEntity.builder().rewardId(dto.getReward().getRewardId()).build())
                .donation(dto.getDonation() != null
                        ? MaterialDonationEntity.builder().donationId(dto.getDonation().getDonationId()).build()
                        : null)
                .amount(dto.getAmount())
                .build();
    }
}
