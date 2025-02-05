package app.scit46.ufc.entity;

import app.scit46.ufc.dto.MaterialDonationDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

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

    // OneToMany: RewardDeliveries.donation 참조
    @OneToMany(mappedBy = "donation", fetch = FetchType.LAZY)
    private List<RewardDeliveryEntity> rewardDeliveries;

    public static MaterialDonationEntity toEntity(MaterialDonationDTO dto, CampaignEntity campaign, UserEntity user, MaterialEntity material) {
        return MaterialDonationEntity.builder()
                .donationId(dto.getDonationId())
                .campaign(campaign)
                .user(user)
                .material(material)
                .quantity(dto.getQuantity())
                .status(dto.getStatus())
                .donatedDate(dto.getDonatedDate())
                .build();
    }
}
