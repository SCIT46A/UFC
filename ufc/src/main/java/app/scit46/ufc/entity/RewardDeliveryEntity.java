package app.scit46.ufc.entity;

import app.scit46.ufc.dto.RewardDeliveryDTO;
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

    @Column(name = "invoice", nullable = false, length = 40)
    private String invoice;

    @Column(name = "status", length = 100)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private CampaignEntity campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id")
    private MaterialDonationEntity donation;

    public static RewardDeliveryEntity toEntity(RewardDeliveryDTO dto, CampaignEntity campaign, MaterialDonationEntity donation) {
        return RewardDeliveryEntity.builder()
                .rDeliveryId(dto.getRDeliveryId())
                .invoice(dto.getInvoice())
                .status(dto.getStatus())
                .campaign(campaign)
                .donation(donation)
                .build();
    }
}
