package app.scit46.ufc.entity.reward;

import app.scit46.ufc.dto.reward.RewardDeliveryDTO;
import app.scit46.ufc.entity.MaterialDonationEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id")
    private MaterialDonationEntity donation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private RewardEntity reward;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    public static RewardDeliveryEntity toEntity(RewardDeliveryDTO dto, RewardEntity reward,
            MaterialDonationEntity donation) {
        return RewardDeliveryEntity.builder()
                .rDeliveryId(dto.getRDeliveryId())
                .invoice(dto.getInvoice())
                .status(dto.getStatus())
                .reward(reward)
                .donation(donation)
                .amount(dto.getAmount())
                .build();
    }
}
