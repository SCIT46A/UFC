package app.scit46.ufc.entity;

import app.scit46.ufc.dto.RewardMaterialDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "RewardMaterials")
public class RewardMaterialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "re_mater_id")
    private Long reMaterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private RewardEntity reward;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private MaterialEntity material;

    @Column(name = "quantity_required", nullable = false)
    private Integer quantityRequired;

    public static RewardMaterialEntity toEntity(RewardMaterialDTO dto, RewardEntity reward, MaterialEntity material) {
        return RewardMaterialEntity.builder()
                .reMaterId(dto.getReMaterId())
                .reward(reward)
                .material(material)
                .quantityRequired(dto.getQuantityRequired())
                .build();
    }
}
