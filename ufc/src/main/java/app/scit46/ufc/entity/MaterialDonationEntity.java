package app.scit46.ufc.entity;

import java.time.LocalDateTime;
import java.util.List;

import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.reward.RewardDeliveryEntity;
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
@Table(name = "MaterialsDonations")
public class MaterialDonationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donation_id")
    private Long donationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private CampaignEntity campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private MaterialEntity material;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "donated_date", nullable = false)
    private LocalDateTime donatedDate;

    @Column(name = "invoice", length = 100, nullable = false)
    private String invoice;

    // OneToMany: RewardDeliveries.donation 참조
    @OneToMany(mappedBy = "donation", fetch = FetchType.LAZY)
    private List<RewardDeliveryEntity> rewardDeliveries;

    public static MaterialDonationEntity toEntity(MaterialDonationDTO dto) {
        return MaterialDonationEntity.builder()
                .donationId(dto.getDonationId())
                .campaign(CampaignEntity.builder().campaignId(dto.getCampaign().getCampaignId()).build())
                .user(UserEntity.builder().userId(dto.getUser().getUserId()).build())
                .material(MaterialEntity.builder().materialId(dto.getMaterial().getMaterialId()).build())
                .quantity(dto.getQuantity())
                .status(dto.getStatus())
                .donatedDate(dto.getDonatedDate())
                .invoice(dto.getInvoice())
                .build();
    }
}
