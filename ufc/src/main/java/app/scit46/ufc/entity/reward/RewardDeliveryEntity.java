package app.scit46.ufc.entity.reward;

import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.reward.RewardDeliveryDTO;
import app.scit46.ufc.entity.MaterialDonationEntity;
import app.scit46.ufc.entity.campaign.CampaignEntity;
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

    public static RewardDeliveryEntity toEntity(RewardDeliveryDTO dto) {
        return RewardDeliveryEntity.builder()
                .rDeliveryId(dto.getRDeliveryId())
                .invoice(dto.getInvoice())
                .status(dto.getStatus())
                .campaign(CampaignEntity.builder().campaignId(dto.getCampaign().getCampaignId()).build())
                .donation(MaterialDonationEntity.builder().donationId(dto.getDonation().getDonationId()).build())
                .build();
    }
}
