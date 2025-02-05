package app.scit46.ufc.entity;

import app.scit46.ufc.dto.MaterialDTO;
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
@Table(name = "Materials")
public class MaterialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Long materialId;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private PublicPhotoEntity photo;

    // OneToMany: CampaignGoals.material 참조
    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
    private List<CampaignGoalEntity> campaignGoals;

    // OneToMany: MaterialsDonations.material 참조
    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
    private List<MaterialDonationEntity> materialDonations;

    // OneToMany: RewardMaterials.material 참조
    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
    private List<RewardMaterialEntity> rewardMaterials;

    public static MaterialEntity toEntity(MaterialDTO dto, PublicPhotoEntity photo) {
        return MaterialEntity.builder()
                .materialId(dto.getMaterialId())
                .name(dto.getName())
                .description(dto.getDescription())
                .photo(photo)
                .build();
    }
}
