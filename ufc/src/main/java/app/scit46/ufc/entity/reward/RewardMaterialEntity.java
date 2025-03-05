package app.scit46.ufc.entity.reward;

import app.scit46.ufc.dto.MaterialDTO;
import app.scit46.ufc.dto.reward.RewardDTO;
import app.scit46.ufc.dto.reward.RewardMaterialDTO;
import app.scit46.ufc.entity.MaterialEntity;
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

    public static RewardMaterialEntity toEntity(RewardMaterialDTO dto) {
        return RewardMaterialEntity.builder()
                .reMaterId(dto.getReMaterId())
                .reward(RewardEntity.toEntity(dto.getReward()))
                .material(MaterialEntity.toEntity(dto.getMaterial()))
                .quantityRequired(dto.getQuantityRequired())
                .build();
    }
}
