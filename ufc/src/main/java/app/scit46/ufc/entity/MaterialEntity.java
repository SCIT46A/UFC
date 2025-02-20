package app.scit46.ufc.entity;

import java.util.List;

import app.scit46.ufc.dto.MaterialDTO;
import app.scit46.ufc.entity.campaign.CampaignGoalEntity;
import app.scit46.ufc.entity.reward.RewardMaterialEntity;
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
@Table(name = "Materials")
public class MaterialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Long materialId;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    //@Column(name = "description", length = 255)
    //private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private ImageUrlEntity photo;

    // OneToMany: CampaignGoals.material 참조
    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
    private List<CampaignGoalEntity> campaignGoals;

    // OneToMany: MaterialsDonations.material 참조
    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
    private List<MaterialDonationEntity> materialDonations;

    // OneToMany: RewardMaterials.material 참조
    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
    private List<RewardMaterialEntity> rewardMaterials;

    public static MaterialEntity toEntity(MaterialDTO dto, ImageUrlEntity photo) {
        return MaterialEntity.builder()
                .materialId(dto.getMaterialId())
                .name(dto.getName())
                //.description(dto.getDescription())
                .photo(photo)
                .build();
    }
}
